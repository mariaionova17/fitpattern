package domain.solver

import domain.fabric.FabricPiece
import domain.geometry.BoundingBox
import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.geometry.Transformation
import domain.geometry.apply
import domain.geometry.area
import domain.geometry.boundingBox
import domain.geometry.contains
import domain.geometry.edges
import domain.geometry.intersects
import domain.geometry.movedBy
import domain.geometry.setSeamAllowance
import domain.layout.GeometryKernel
import domain.layout.LayoutScore
import domain.layout.LayoutState
import domain.layout.Placement
import domain.pattern.PatternPiece
import domain.pattern.PlacementRules
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.time.TimeSource

// ---------------------------------------------------------------------------
// Data structures
// ---------------------------------------------------------------------------



// ---------------------------------------------------------------------------
// Solver
// ---------------------------------------------------------------------------

class NotBetterSolver(
    private val geometry: GeometryKernel,
    private val maxContourAnchors: Int = 12,
    private val beamWidth: Int = 3
) : NestingSolver {

    // NFP and IFP caches — survive for the lifetime of one solve() call
    private val nfpCache = mutableMapOf<NfpKey, List<Polygon>>()
    private val ifpCache = mutableMapOf<Pair<String, Double>, List<Point>>()

    override fun solve(problem: NestingProblem): NestingResult {
        val start = TimeSource.Monotonic.markNow()
        nfpCache.clear()
        ifpCache.clear()

        require(problem.project.fabricPieces.isNotEmpty()) { "No fabric pieces" }

        val orderedPatternPieces = orderPatternPieces(
            problem.pieces.map { piece ->
                piece.copy(shape = piece.shape.setSeamAllowance(problem.project.seamAllowance))
            }
        )

        val orientedPiecesById = precomputeOrientedPieces(
            pieces = orderedPatternPieces,
            rules = problem.project.rules
        )

        // Pre-compute IFP candidate points for each (fabric, oriented piece) pair
        // so we don't redo this inside the hot loop
        val ifpPointsCache = mutableMapOf<Pair<Int, NfpKey>, List<Point>>()
        for ((fabricIndex, fabricPiece) in problem.project.fabricPieces.withIndex()) {
            for (piece in orderedPatternPieces) {
                for (oriented in orientedPiecesById[piece.id] ?: emptyList()) {
                    val key = Pair(fabricIndex, NfpKey(fabricPiece.id, 0.0, piece.id, oriented.angle))
                    ifpPointsCache[key] = computeIFPPoints(fabricPiece, oriented)
                }
            }
        }

        var beam = listOf(
            PartialLayout(fabrics = problem.project.fabricPieces.map { FabricLayoutState() })
        )
        var triedLayouts = 0

        for (piece in orderedPatternPieces) {
            val nextBeam = mutableListOf<PartialLayout>()

            for (layout in beam) {
                var placedSomewhere = false

                for (fabricIndex in problem.project.fabricPieces.indices) {
                    val fabricPiece = problem.project.fabricPieces[fabricIndex]
                    val fabricState = layout.fabrics[fabricIndex]
                    val orientedPieces = orientedPiecesById[piece.id] ?: emptyList()

                    val candidatePlacement = findBestPlacement(
                        piece = piece,
                        fabricPiece = fabricPiece,
                        fabricIndex = fabricIndex,
                        fabricState = fabricState,
                        orientedPieces = orientedPieces,
                        ifpPointsCache = ifpPointsCache
                    )

                    if (candidatePlacement != null) {
                        placedSomewhere = true
                        triedLayouts++

                        val bb = candidatePlacement.boundingBox
                        val newTargets = (fabricState.targetPoints +
                                candidatePlacement.shape.contour.points)
                            .distinctBy { packedKey(it) }

                        val updatedFabricState = fabricState.copy(
                            placedShapes = fabricState.placedShapes + candidatePlacement.shape,
                            placedBoundingBoxes = fabricState.placedBoundingBoxes + bb,
                            placements = fabricState.placements + Placement(
                                id = "",
                                pieceId = piece.id,
                                fabricPieceIndex = fabricIndex,
                                transformation = candidatePlacement.transformation
                            ),
                            extMinX = min(fabricState.extMinX, bb.minX),
                            extMinY = min(fabricState.extMinY, bb.minY),
                            extMaxX = max(fabricState.extMaxX, bb.maxX),
                            extMaxY = max(fabricState.extMaxY, bb.maxY),
                            targetPoints = newTargets
                        )

                        val updatedFabrics = layout.fabrics.toMutableList()
                        updatedFabrics[fabricIndex] = updatedFabricState

                        nextBeam += PartialLayout(
                            fabrics = updatedFabrics,
                            unplaced = layout.unplaced,
                            score = calculateMultiFabricScore(updatedFabrics)
                        )
                    }
                }

                if (!placedSomewhere) {
                    nextBeam += layout.copy(
                        unplaced = layout.unplaced + piece.id,
                        score = calculateMultiFabricScore(layout.fabrics)
                    )
                }
            }

            beam = nextBeam
                .sortedBy { layoutToScalarScore(it) }
                .take(beamWidth)
        }

        val bestLayout = beam.minByOrNull { layoutToScalarScore(it) }
            ?: PartialLayout(fabrics = problem.project.fabricPieces.map { FabricLayoutState() })

        return NestingResult(
            id = "",
            bestLayout = LayoutState(
                placed = bestLayout.fabrics.flatMap { it.placements },
                unplaced = bestLayout.unplaced,
                score = bestLayout.score
            ),
            triedLayouts = triedLayouts,
            timeMs = start.elapsedNow().inWholeMilliseconds
        )
    }

    // ---------------------------------------------------------------------------
    // Core placement search — NFP/IFP driven
    // ---------------------------------------------------------------------------

    private fun findBestPlacement(
        piece: PatternPiece,
        fabricPiece: FabricPiece,
        fabricIndex: Int,
        fabricState: FabricLayoutState,
        orientedPieces: List<OrientedPiece>,
        ifpPointsCache: Map<Pair<Int, NfpKey>, List<Point>>
    ): CandidatePlacement? {
        val fabricBoundingBox = fabricPiece.contour.boundingBox()
        var bestPlacement: CandidatePlacement? = null
        var trialsWithoutImprovement = 0

        for (oriented in orientedPieces) {
            // --- Candidate reference positions ---
            // 1. IFP vertices: positions where piece touches the fabric boundary
            val ifpKey = Pair(fabricIndex, NfpKey(fabricPiece.id, 0.0, piece.id, oriented.angle))
            val ifpPoints = ifpPointsCache[ifpKey] ?: emptyList()

            // 2. NFP vertices against each placed piece: positions where new piece
            //    snugly touches an already-placed piece (no gap, no overlap)
            val nfpPoints = mutableListOf<Point>()
            for (placedShape in fabricState.placedShapes) {
                val nfpKey = NfpKey(
                    fixedId = "placed", fixedAngle = 0.0,
                    movingId = piece.id, movingAngle = oriented.angle
                )
                val nfp = nfpCache.getOrPut(nfpKey) {
                    computeNFP(placedShape, oriented)
                }
                nfpPoints += nfp.flatMap { it.points }
            }

            // 3. Cached target points from placed pieces (fallback for first piece)
            val targetPoints = if (fabricState.placedShapes.isEmpty()) {
                ifpPoints
            } else {
                ifpPoints + nfpPoints
            }

            for (refPoint in targetPoints) {
                // Translate the oriented shape so its reference point lands on refPoint
                val orientedRef = oriented.shape.contour.points.first()
                val offset = Point(
                    x = refPoint.x - orientedRef.x,
                    y = refPoint.y - orientedRef.y
                )

                val movedBB = oriented.boundingBox.movedBy(offset)

                // Fast reject: bounding box must be inside fabric bounding box
                if (!fabricBoundingBox.contains(movedBB)) continue

                val placedShape = oriented.shape.apply(Transformation(move = offset))

                // Full containment check
                if (!geometry.contains(fabricPiece.contour, placedShape)) continue

                // Overlap check against placed pieces (BB pre-filter)
                var overlaps = false
                for (i in fabricState.placedShapes.indices) {
                    if (!movedBB.intersects(fabricState.placedBoundingBoxes[i])) continue
                    if (geometry.intersects(placedShape, fabricState.placedShapes[i])) {
                        overlaps = true
                        break
                    }
                }
                if (overlaps) continue

                val placedBB = placedShape.boundingBox()
                val score = calculateCandidateScore(
                    candidateBB = placedBB,
                    fabricState = fabricState,
                    fabricBoundingBox = fabricBoundingBox
                )

                if (bestPlacement != null && score.total >= bestPlacement.score.total) {
                    trialsWithoutImprovement++
                    // Early exit: if we haven't improved in a while, this orientation
                    // is unlikely to beat current best
                    if (trialsWithoutImprovement > 12) break
                    continue
                }

                trialsWithoutImprovement = 0

                // Compute transformation relative to the original (un-rotated) shape
                val originalBB = oriented.boundingBox
                val finalOffset = Point(
                    x = placedBB.minX - originalBB.minX,
                    y = placedBB.minY - originalBB.minY
                )

                bestPlacement = CandidatePlacement(
                    transformation = Transformation(
                        move = finalOffset,
                        rotationDegrees = oriented.angle,
                        mirrorY = false
                    ),
                    shape = placedShape,
                    boundingBox = placedBB,
                    score = score
                )
            }
        }

        return bestPlacement
    }

    // ---------------------------------------------------------------------------
    // IFP — Inner Fit Polygon
    // All positions where a piece's reference point can sit such that the piece
    // is fully inside the fabric. For a convex fabric this is exact; for concave
    // fabric we use the Minkowski difference approximation and then filter with
    // the real containment check.
    // ---------------------------------------------------------------------------

    private fun computeIFPPoints(
        fabricPiece: FabricPiece,
        oriented: OrientedPiece
    ): List<Point> {
        val fabricContour = fabricPiece.contour.contour
        val pieceContour = oriented.shape.contour

        // Reflect piece through its reference point (first contour point)
        val ref = pieceContour.points.first()
        val reflected = pieceContour.points.map { p ->
            Point(2 * ref.x - p.x, 2 * ref.y - p.y)
        }

        // Minkowski sum of fabric contour + reflected piece = IFP (for convex shapes)
        val ifpPolygon = minkowskiSumConvex(
            fabricContour.points,
            reflected
        )

        // For non-convex fabric: use fabric vertices + midpoints as fallback anchors,
        // the full containment check in findBestPlacement will filter invalid ones
        val candidates = if (ifpPolygon != null) {
            ifpPolygon.points + edgeMidpoints(ifpPolygon)
        } else {
            fabricContour.points + edgeMidpoints(fabricContour)
        }

        return candidates.distinctBy { packedKey(it) }
    }

    // ---------------------------------------------------------------------------
    // NFP — No-Fit Polygon
    // All positions for the moving piece's reference point where it exactly
    // touches (but does not overlap) the fixed piece.
    //
    // Strategy:
    //   1. Decompose both pieces into convex parts
    //   2. Compute Minkowski sum for each pair of (fixed part, reflected moving part)
    //   3. Union all resulting polygons — the boundary vertices are all valid
    //      "touching" placements
    //
    // For mobile we skip the true polygon union and just collect all boundary
    // vertices; the overlap check in findBestPlacement discards invalid ones.
    // ---------------------------------------------------------------------------

    private fun computeNFP(
        fixedShape: Shape,
        movingOriented: OrientedPiece
    ): List<Polygon> {
        val fixedParts = approximateConvexDecomposition(fixedShape.contour)
        val movingParts = movingOriented.convexParts

        val nfpPolygons = mutableListOf<Polygon>()

        for (fixedPart in fixedParts) {
            for (movingPart in movingParts) {
                // Reflect moving part through its first point
                val ref = movingPart.points.first()
                val reflected = movingPart.points.map { p ->
                    Point(2 * ref.x - p.x, 2 * ref.y - p.y)
                }
                val minkSum = minkowskiSumConvex(fixedPart.points, reflected)
                if (minkSum != null) nfpPolygons += minkSum
            }
        }

        return nfpPolygons
    }

    // ---------------------------------------------------------------------------
    // Minkowski sum of two convex polygons
    // Both input lists must be in counter-clockwise order.
    // Returns null if either polygon is degenerate.
    // ---------------------------------------------------------------------------

    private fun minkowskiSumConvex(a: List<Point>, b: List<Point>): Polygon? {
        if (a.size < 3 || b.size < 3) return null

        // Ensure CCW
        val aCCW = if (signedArea(a) < 0) a.reversed() else a
        val bCCW = if (signedArea(b) < 0) b.reversed() else b

        // Find bottom-most (then left-most) point in each polygon
        fun bottomMostIndex(pts: List<Point>): Int =
            pts.indices.minByOrNull { pts[it].y * 10000 + pts[it].x } ?: 0

        val ai = bottomMostIndex(aCCW)
        val bi = bottomMostIndex(bCCW)

        val result = mutableListOf<Point>()
        var i = 0
        var j = 0
        val na = aCCW.size
        val nb = bCCW.size

        // Walk edges by angle (rotating calipers style)
        while (i < na || j < nb) {
            result += Point(
                x = aCCW[(ai + i) % na].x + bCCW[(bi + j) % nb].x,
                y = aCCW[(ai + i) % na].y + bCCW[(bi + j) % nb].y
            )

            val edgeA = edgeVector(aCCW, (ai + i) % na)
            val edgeB = edgeVector(bCCW, (bi + j) % nb)
            val cross = edgeA.x * edgeB.y - edgeA.y * edgeB.x

            when {
                i >= na -> j++
                j >= nb -> i++
                cross > 0 -> i++   // edge A comes first angularly
                cross < 0 -> j++   // edge B comes first
                else -> { i++; j++ }   // parallel edges: advance both
            }
        }

        return if (result.size >= 3) Polygon(result) else null
    }

    private fun edgeVector(pts: List<Point>, i: Int): Point {
        val next = pts[(i + 1) % pts.size]
        return Point(next.x - pts[i].x, next.y - pts[i].y)
    }

    private fun signedArea(pts: List<Point>): Double {
        var area = 0.0
        val n = pts.size
        for (i in pts.indices) {
            val j = (i + 1) % n
            area += pts[i].x * pts[j].y - pts[j].x * pts[i].y
        }
        return area / 2.0
    }

    // ---------------------------------------------------------------------------
    // Approximate convex decomposition
    // Splits a concave polygon into convex parts by cutting at reflex vertices.
    // "Reflex" = interior angle > 180°, i.e. the cross product of consecutive
    // edges is negative (for a CCW polygon).
    // This is a greedy single-pass approach — good enough for sewing pieces which
    // typically have only 1-3 concavities (darts, notches, armscyes).
    // ---------------------------------------------------------------------------

    private fun approximateConvexDecomposition(polygon: Polygon): List<Polygon> {
        val pts = polygon.points
        if (pts.size < 3) return emptyList()

        // If already convex, return as-is
        if (isConvex(pts)) return listOf(polygon)

        val parts = mutableListOf<Polygon>()
        val remaining = pts.toMutableList()

        var safetyCounter = 0
        while (remaining.size >= 3 && safetyCounter++ < pts.size * 2) {
            val reflexIdx = findReflexVertex(remaining) ?: break

            // Cut: connect the previous and next vertices of the reflex point,
            // producing a triangular ear and the remaining polygon
            val prev = (reflexIdx - 1 + remaining.size) % remaining.size
            val next = (reflexIdx + 1) % remaining.size

            // Ear triangle: prev, reflex, next
            parts += Polygon(listOf(remaining[prev], remaining[reflexIdx], remaining[next]))

            // Remove the reflex vertex from remaining
            remaining.removeAt(reflexIdx)
        }

        if (remaining.size >= 3) parts += Polygon(remaining)

        // Filter degenerate parts
        return parts.filter { it.points.size >= 3 && abs(signedArea(it.points)) > 0.01 }
    }

    private fun isConvex(pts: List<Point>): Boolean {
        val n = pts.size
        var sign = 0
        for (i in pts.indices) {
            val o = pts[i]
            val a = pts[(i + 1) % n]
            val b = pts[(i + 2) % n]
            val cross = (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x)
            if (cross != 0.0) {
                val s = if (cross > 0) 1 else -1
                if (sign == 0) sign = s else if (sign != s) return false
            }
        }
        return true
    }

    private fun findReflexVertex(pts: List<Point>): Int? {
        val n = pts.size
        // Ensure CCW so reflex = negative cross product
        val ccw = signedArea(pts) > 0
        for (i in pts.indices) {
            val prev = pts[(i - 1 + n) % n]
            val curr = pts[i]
            val next = pts[(i + 1) % n]
            val cross = (curr.x - prev.x) * (next.y - curr.y) -
                    (curr.y - prev.y) * (next.x - curr.x)
            val isReflex = if (ccw) cross < 0 else cross > 0
            if (isReflex) return i
        }
        return null
    }

    // ---------------------------------------------------------------------------
    // Scoring — uses incremental extents from FabricLayoutState
    // ---------------------------------------------------------------------------

    private fun calculateCandidateScore(
        candidateBB: BoundingBox,
        fabricState: FabricLayoutState,
        fabricBoundingBox: BoundingBox
    ): CandidateScore {
        // Use running extents — O(1), no scanning
        val minX = if (fabricState.extMinX == Double.MAX_VALUE) candidateBB.minX
        else min(fabricState.extMinX, candidateBB.minX)
        val minY = if (fabricState.extMinY == Double.MAX_VALUE) candidateBB.minY
        else min(fabricState.extMinY, candidateBB.minY)
        val maxX = if (fabricState.extMaxX == Double.MIN_VALUE) candidateBB.maxX
        else max(fabricState.extMaxX, candidateBB.maxX)
        val maxY = if (fabricState.extMaxY == Double.MIN_VALUE) candidateBB.maxY
        else max(fabricState.extMaxY, candidateBB.maxY)

        val boundingArea = (maxX - minX) * (maxY - minY)

        val borderReward = estimateBorderReward(candidateBB, fabricBoundingBox)
        val contactReward = estimateContactReward(candidateBB, fabricState.placedBoundingBoxes)
        val floatingPenalty = estimateFloatingPenalty(
            candidateBB, fabricState.placedBoundingBoxes, fabricBoundingBox
        )

        val total = boundingArea -
                contactReward * 400.0 -
                borderReward * 300.0 +
                floatingPenalty * 300.0 +
                maxY * 20.0 +
                maxX * 5.0

        return CandidateScore(
            total = total,
            boundingArea = boundingArea,
            borderReward = borderReward,
            pieceContactReward = contactReward,
            floatingPenalty = floatingPenalty
        )
    }

    private fun estimateBorderReward(bb: BoundingBox, fabric: BoundingBox): Double {
        val eps = 3.0
        var r = 0.0
        if (abs(bb.minX - fabric.minX) <= eps) r += 1.0
        if (abs(bb.maxX - fabric.maxX) <= eps) r += 1.0
        if (abs(bb.minY - fabric.minY) <= eps) r += 1.0
        if (abs(bb.maxY - fabric.maxY) <= eps) r += 1.0
        return r
    }

    private fun estimateContactReward(bb: BoundingBox, placed: List<BoundingBox>): Double {
        val eps = 3.0
        var r = 0.0
        for (box in placed) {
            if (abs(bb.minX - box.maxX) <= eps) r += 1.0
            if (abs(bb.maxX - box.minX) <= eps) r += 1.0
            if (abs(bb.minY - box.maxY) <= eps) r += 1.0
            if (abs(bb.maxY - box.minY) <= eps) r += 1.0
        }
        return r
    }

    private fun estimateFloatingPenalty(
        bb: BoundingBox,
        placed: List<BoundingBox>,
        fabric: BoundingBox
    ): Double {
        val minFabricGap = minOf(
            bb.minX - fabric.minX,
            fabric.maxX - bb.maxX,
            bb.minY - fabric.minY,
            fabric.maxY - bb.maxY
        )
        if (placed.isEmpty()) return minFabricGap

        var minNeighborGap = Double.MAX_VALUE
        for (box in placed) {
            val dx = min(abs(bb.minX - box.maxX), abs(bb.maxX - box.minX))
            val dy = min(abs(bb.minY - box.maxY), abs(bb.maxY - box.minY))
            minNeighborGap = min(minNeighborGap, min(dx, dy))
        }
        return min(minFabricGap, minNeighborGap)
    }

    private fun calculateMultiFabricScore(fabrics: List<FabricLayoutState>): LayoutScore {
        var totalHeight = 0.0
        var totalWidth = 0.0
        for (fabric in fabrics) {
            if (fabric.extMaxX == Double.MIN_VALUE) continue
            totalWidth += fabric.extMaxX - fabric.extMinX
            totalHeight += fabric.extMaxY - fabric.extMinY
        }
        return LayoutScore(height = totalHeight, width = totalWidth)
    }

    private fun layoutToScalarScore(layout: PartialLayout): Double {
        val usedFabrics = layout.fabrics.count { it.placements.isNotEmpty() }
        return usedFabrics * 1_000_000.0 +
                layout.score.height * 1_000.0 +
                layout.score.width +
                layout.unplaced.size * 100_000_000.0
    }

    // ---------------------------------------------------------------------------
    // Piece ordering — largest area first
    // ---------------------------------------------------------------------------

    private fun orderPatternPieces(pieces: List<PatternPiece>): List<PatternPiece> =
        pieces.sortedWith(
            compareByDescending<PatternPiece> { it.shape.contour.area() }
                .thenByDescending {
                    val bb = it.shape.boundingBox()
                    max(bb.maxX - bb.minX, bb.maxY - bb.minY)
                }
        )

    // ---------------------------------------------------------------------------
    // Pre-compute oriented pieces (rotation applied, convex parts cached)
    // ---------------------------------------------------------------------------

    private fun precomputeOrientedPieces(
        pieces: List<PatternPiece>,
        rules: PlacementRules
    ): Map<String, List<OrientedPiece>> =
        pieces.associate { piece ->
            val versions = rules.rotation.allowedAnglesDeg.map { angle ->
                val rotated = piece.shape.apply(
                    Transformation(
                        move = Point(0.0, 0.0),
                        rotationDegrees = angle,
                        mirrorY = false
                    )
                )
                val bb = rotated.boundingBox()
                val convexParts = approximateConvexDecomposition(rotated.contour)

                OrientedPiece(
                    pieceId = piece.id,
                    angle = angle,
                    shape = rotated,
                    boundingBox = bb,
                    convexParts = convexParts
                )
            }
            piece.id to versions
        }

    // ---------------------------------------------------------------------------
    // Utilities
    // ---------------------------------------------------------------------------

    private fun edgeMidpoints(polygon: Polygon): List<Point> {
        val pts = polygon.points
        return pts.indices.map { i ->
            val a = pts[i]
            val b = pts[(i + 1) % pts.size]
            Point((a.x + b.x) / 2.0, (a.y + b.y) / 2.0)
        }
    }

    /** Pack a Point into a Long for O(1) deduplication — avoids Pair allocation. */
    private fun packedKey(p: Point): Long {
        val x = kotlin.math.round(p.x * 100).toLong().coerceIn(-999_999, 999_999)
        val y = kotlin.math.round(p.y * 100).toLong().coerceIn(-999_999, 999_999)
        return (x + 999_999) * 2_000_000L + (y + 999_999)
    }

    private fun BoundingBox.movedBy(offset: Point) = BoundingBox(
        minX = minX + offset.x,
        minY = minY + offset.y,
        maxX = maxX + offset.x,
        maxY = maxY + offset.y
    )

    private fun BoundingBox.contains(other: BoundingBox) =
        other.minX >= minX && other.minY >= minY &&
                other.maxX <= maxX && other.maxY <= maxY

    private fun BoundingBox.intersects(other: BoundingBox) =
        minX < other.maxX && maxX > other.minX &&
                minY < other.maxY && maxY > other.minY



    data class FabricLayoutState(
        val placedShapes: List<Shape> = emptyList(),
        val placedBoundingBoxes: List<BoundingBox> = emptyList(),
        val placements: List<Placement> = emptyList(),
        // Running extents — updated incrementally, never scanned
        val extMinX: Double = Double.MAX_VALUE,
        val extMinY: Double = Double.MAX_VALUE,
        val extMaxX: Double = Double.MIN_VALUE,
        val extMaxY: Double = Double.MIN_VALUE,
        // Cached target points — appended to, never rebuilt from scratch
        val targetPoints: List<Point> = emptyList()
    )

    private data class PartialLayout(
        val fabrics: List<FabricLayoutState>,
        val unplaced: List<String> = emptyList(),
        val score: LayoutScore = LayoutScore(0.0, 0.0)
    )

    private data class OrientedPiece(
        val pieceId: String,
        val angle: Double,
        val shape: Shape,
        val boundingBox: BoundingBox,
        // Convex parts used for NFP computation
        val convexParts: List<Polygon>
    )

    private data class CandidatePlacement(
        val transformation: Transformation,
        val shape: Shape,
        val boundingBox: BoundingBox,
        val score: CandidateScore
    )

    private data class CandidateScore(
        val total: Double,
        val boundingArea: Double,
        val borderReward: Double,
        val pieceContactReward: Double,
        val floatingPenalty: Double
    ) : Comparable<CandidateScore> {
        override fun compareTo(other: CandidateScore) = total.compareTo(other.total)
    }

    // NFP cache key: oriented piece ids + angles
    private data class NfpKey(
        val fixedId: String, val fixedAngle: Double,
        val movingId: String, val movingAngle: Double
    )
}
