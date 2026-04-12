package domain.solver

import domain.fabric.FabricPiece
import domain.geometry.BoundingBox
import domain.geometry.Point
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
import kotlin.math.max
import kotlin.time.TimeSource

private data class FabricLayoutState(
    val placedShapes: List<Shape> = emptyList(),
    val placedBoundingBoxes: List<BoundingBox> = emptyList(),
    val placements: List<Placement> = emptyList()
)

private data class PartialLayout(
    val fabrics: List<FabricLayoutState>,
    val unplaced: List<String> = emptyList(),
    val score: LayoutScore = LayoutScore(0.0, 0.0)
)
// for caching rotated pieces
private data class OrientedPiece(
    val pieceId: String,
    val angle: Double,
    val shape: Shape,
    val boundingBox: BoundingBox,
    val anchors: List<Point>
)

class BetterSolver(private val geometry: GeometryKernel, private val maxContourAnchors: Int = 10, private val candidatesPerFabric: Int = 3) : NestingSolver {

    override fun solve(problem: NestingProblem): NestingResult {
        val start = TimeSource.Monotonic.markNow()
        require(problem.project.fabricPieces.isNotEmpty()) { "No fabric pieces" }

        val orderedPatternPieces = orderPatternPieces(
            problem.pieces.map { piece ->
                piece.copy(
                    shape = piece.shape.setSeamAllowance(problem.project.seamAllowance)
                )
            }
        )

        val orientedPiecesById = precomputeOrientedPieces(
            pieces = orderedPatternPieces,
            rules = problem.project.rules
        )

        val initialPartialLayout = PartialLayout(
            fabrics = problem.project.fabricPieces.map { FabricLayoutState() },
            unplaced = emptyList(),
            score = LayoutScore(0.0, 0.0)
        )

        var beam = listOf(initialPartialLayout)
        var triedLayouts = 0
        val beamWidth = 5

        for (piece in orderedPatternPieces) {
            val nextBeam = mutableListOf<PartialLayout>()

            for (layout in beam) {
                var placedSomewhere = false

                for (fabricIndex in problem.project.fabricPieces.indices) {
                    val fabricPiece = problem.project.fabricPieces[fabricIndex]
                    val fabricBoundingBox = fabricPiece.contour.boundingBox()
                    val fabricState = layout.fabrics[fabricIndex]

                    val candidatePlacements = findBestPlacement(
                        piece = piece,
                        fabricPiece = fabricPiece,
                        fabricBoundingBox = fabricBoundingBox,
                        placedShapes = fabricState.placedShapes,
                        placedBoundingBoxes = fabricState.placedBoundingBoxes,
                        orientedPieces = orientedPiecesById[piece.id] ?: emptyList(),
                    )

                    if (candidatePlacements.isNotEmpty()) {
                        placedSomewhere = true

                        for (candidatePlacement in candidatePlacements) {
                            triedLayouts++

                            val updatedFabricState = fabricState.copy(
                                placedShapes = fabricState.placedShapes + candidatePlacement.shape,
                                placedBoundingBoxes = fabricState.placedBoundingBoxes + candidatePlacement.boundingBox,
                                placements = fabricState.placements + Placement(
                                    pieceId = piece.id,
                                    fabricPieceIndex = fabricIndex,
                                    transformation = candidatePlacement.transformation
                                )
                            )

                            val updatedFabrics = layout.fabrics.toMutableList()
                            updatedFabrics[fabricIndex] = updatedFabricState

                            val newScore = calculateMultiFabricLayoutScore(updatedFabrics)

                            nextBeam += PartialLayout(
                                fabrics = updatedFabrics,
                                unplaced = layout.unplaced,
                                score = newScore
                            )
                        }
                    }
                }

                if (!placedSomewhere) {
                    nextBeam += layout.copy(
                        unplaced = layout.unplaced + piece.id,
                        score = calculateMultiFabricLayoutScore(layout.fabrics)
                    )
                }
            }

            beam = nextBeam
                .sortedBy { layoutToScalarScore(it, problem.project.fabricPieces) }
                .take(beamWidth)
        }

        val bestLayout = beam.minByOrNull { layoutToScalarScore(it, problem.project.fabricPieces) } ?: initialPartialLayout

        val allPlacements = bestLayout.fabrics.flatMap { it.placements }

        val timeMs = start.elapsedNow().inWholeMilliseconds

        return NestingResult(
            bestLayout = LayoutState(
                placed = allPlacements,
                unplaced = bestLayout.unplaced,
                score = bestLayout.score
            ),
            triedLayouts = triedLayouts,
            timeMs = timeMs
        )
    }


    private fun calculateMultiFabricLayoutScore(fabrics: List<FabricLayoutState>): LayoutScore {
        var totalHeight = 0.0
        var totalWidth = 0.0

        for (fabric in fabrics) {
            if (fabric.placedBoundingBoxes.isEmpty()) continue

            val minX = fabric.placedBoundingBoxes.minOf { it.minX }
            val minY = fabric.placedBoundingBoxes.minOf { it.minY }
            val maxX = fabric.placedBoundingBoxes.maxOf { it.maxX }
            val maxY = fabric.placedBoundingBoxes.maxOf { it.maxY }

            totalWidth += (maxX - minX)
            totalHeight += (maxY - minY)
        }

        return LayoutScore(
            height = totalHeight,
            width = totalWidth
        )
    }

    private fun layoutToScalarScore(
        layout: PartialLayout,
        fabricPieces: List<FabricPiece>
    ): Double {
        val usedFabricCount = layout.fabrics.count { it.placements.isNotEmpty() }

        var total =
            usedFabricCount * 1_000_000.0 +
                    layout.unplaced.size * 100_000_000.0 +
                    layout.score.height * 1_000.0 +
                    layout.score.width

        for (i in layout.fabrics.indices) {
            val fabricState = layout.fabrics[i]
            if (fabricState.placedBoundingBoxes.isEmpty()) continue

            val fabricBox = fabricPieces[i].contour.boundingBox()
            val averageCenterY = fabricState.placedBoundingBoxes
                .map { (it.minY + it.maxY) / 2.0 }
                .average()

            val bottomGap = averageCenterY - fabricBox.minY
            total += bottomGap * 5_000.0
        }

        return total
    }

    private fun findBestPlacement(
        piece: PatternPiece,
        fabricPiece: FabricPiece,
        fabricBoundingBox: BoundingBox,
        placedShapes: List<Shape>,
        placedBoundingBoxes: List<BoundingBox>,
        orientedPieces: List<OrientedPiece>,
    ): List<CandidatePlacement> {

        val targetPoints = getTargetPointsSmart(fabricPiece.contour, placedShapes)
        val candidates = mutableListOf<CandidatePlacement>()
        var bestRawTotalSoFar = Double.POSITIVE_INFINITY

        val rawCandidates = mutableListOf<CandidatePlacement>()

        for (oriented in orientedPieces) {
            val orientation = oriented.angle
            val orientedShape = oriented.shape
            val orientedBoundingBox = oriented.boundingBox
            val anchors = oriented.anchors

            for (anchor in anchors) {
                for (target in targetPoints) {
                    val offset = Point(
                        x = target.x - anchor.x,
                        y = target.y - anchor.y
                    )

                    val movedBoundingBox = orientedBoundingBox.movedBy(offset)

                    // if bb is not inside bb of the fabric
                    if (!fabricBoundingBox.contains(movedBoundingBox)) {
                        continue
                    }

                    val placedShape = orientedShape.apply(Transformation(move = offset))

                    val placedBoundingBox = placedShape.boundingBox()

                    if (!geometry.contains(fabricPiece.contour, placedShape)) {
                        continue
                    }

                    var intersectsPlacedPiece = false

                    for (i in placedShapes.indices) {
                        if (!placedBoundingBox.intersects(placedBoundingBoxes[i])) {
                            continue
                        }

                        if (geometry.intersects(placedShape, placedShapes[i])) {
                            intersectsPlacedPiece = true
                            break
                        }
                    }

                    if (intersectsPlacedPiece) {
                        continue
                    }

                    val rawScore = calculateCandidateScore(
                        candidateBoundingBox = placedBoundingBox,
                        placedBoundingBoxes = placedBoundingBoxes,
                        fabricBoundingBox = fabricBoundingBox
                    )

                    rawCandidates += CandidatePlacement(
                        transformation = Transformation(
                            move = offset,
                            rotationDegrees = orientation,
                            mirrorY = false
                        ),
                        shape = placedShape,
                        boundingBox = placedBoundingBox,
                        score = rawScore,
                        orientedBoundingBox = orientedBoundingBox
                    )
                }
            }
        }
        return rawCandidates
            .sortedBy { it.score }
            .distinctBy { candidateKey(it) }
            .take(5)
            .map { candidate ->
                val refinedShape = refinePlacement(
                    placedShape = candidate.shape,
                    fabricPiece = fabricPiece,
                    placedShapes = placedShapes,
                    placedBoundingBoxes = placedBoundingBoxes
                )
                val refinedBoundingBox = refinedShape.boundingBox()
                CandidatePlacement(
                    transformation = Transformation(
                        move = Point(
                            x = refinedBoundingBox.minX - candidate.orientedBoundingBox.minX,
                            y = refinedBoundingBox.minY - candidate.orientedBoundingBox.minY
                        ),
                        rotationDegrees = candidate.transformation.rotationDegrees,
                        mirrorY = false
                    ),
                    shape = refinedShape,
                    boundingBox = refinedBoundingBox,
                    score = calculateCandidateScore(
                        candidateBoundingBox = refinedBoundingBox,
                        placedBoundingBoxes = placedBoundingBoxes,
                        fabricBoundingBox = fabricBoundingBox
                    ),
                    orientedBoundingBox = candidate.orientedBoundingBox
                )
            }
            .sortedBy { it.score }
            .take(candidatesPerFabric)
    }

    //points are considered equal if they have less of a 0.1 difference
    private fun shortenNumbers(point: Point, cutSize: Int = 100): Pair<Long, Long> =
        Pair(
            kotlin.math.round(point.x * cutSize).toLong(),
            kotlin.math.round(point.y * cutSize).toLong()
        )

    private fun makeAnchors(shape: Shape, boundingBox: BoundingBox): List<Point> {
        val boundingBoxPoints = listOf(
            Point(boundingBox.minX, boundingBox.minY),
            Point(boundingBox.minX, boundingBox.maxY),
            Point(boundingBox.maxX, boundingBox.minY),
            Point(boundingBox.maxX, boundingBox.maxY)
        )

        val contourPoints = shape.contour.points
        val sampledContourPoints =
            if (contourPoints.size <= maxContourAnchors) {
                contourPoints
            } else {
                val step = max(1, contourPoints.size / maxContourAnchors)
                contourPoints.filterIndexed { index, _ -> index % step == 0 }
                    .take(maxContourAnchors)
            }

        return (boundingBoxPoints + sampledContourPoints).distinctBy { shortenNumbers(it) }
    }

    private fun calculateCandidateScore(
        candidateBoundingBox: BoundingBox,
        placedBoundingBoxes: List<BoundingBox>,
        fabricBoundingBox: BoundingBox
    ): CandidateScore {
        val allBoxes = placedBoundingBoxes + candidateBoundingBox

        val minX = allBoxes.minOf { it.minX }
        val minY = allBoxes.minOf { it.minY }
        val maxX = allBoxes.maxOf { it.maxX }
        val maxY = allBoxes.maxOf { it.maxY }

        val width = maxX - minX
        val height = maxY - minY
        val boundingArea = width * height

        val borderGap = nearestBorderGap(candidateBoundingBox, fabricBoundingBox)
        val pieceGap = nearestPieceGap(candidateBoundingBox, placedBoundingBoxes)
        val contacts = contactCount(candidateBoundingBox, placedBoundingBoxes, fabricBoundingBox)

        val hasPlacedPieces = placedBoundingBoxes.isNotEmpty()

        val supportPenalty =
            if (hasPlacedPieces) {
                // once cluster exists, prefer staying close to pieces
                pieceGap * 500.0 + minOf(borderGap, pieceGap) * 150.0
            } else {
                // first piece should anchor to border
                borderGap * 500.0
            }

        val expansionPenalty =
            boundingArea * 1.0 + height * 40.0 + width * 10.0

        val contactBonus = contacts * 350.0

        val averageCenterY = allBoxes.map { (it.minY + it.maxY) / 2.0 }.average()
        val averageBottomGap = averageCenterY - fabricBoundingBox.minY
        val bottomBiasPenalty = averageBottomGap * 500.0

        val total =
            expansionPenalty +
                    supportPenalty +
                    bottomBiasPenalty -
                    contactBonus

        return CandidateScore(
            total = total,
            boundingArea = boundingArea,
            borderReward = 0.0,
            pieceContactReward = contacts,
            floatingPenalty = minOf(borderGap, pieceGap)
        )
    }

    private data class CandidatePlacement(
        val transformation: Transformation,
        val shape: Shape,
        val boundingBox: BoundingBox,
        val score: CandidateScore,
        val orientedBoundingBox: BoundingBox
    )

    private data class CandidateScore(
        val total: Double,
        val boundingArea: Double,
        val borderReward: Double,
        val pieceContactReward: Double,
        val floatingPenalty: Double
    ) : Comparable<CandidateScore> {
        override fun compareTo(other: CandidateScore): Int {
            return total.compareTo(other.total)
        }
    }

    private fun refinePlacement(
        placedShape: Shape,
        fabricPiece: FabricPiece,
        placedShapes: List<Shape>,
        placedBoundingBoxes: List<BoundingBox>,
        step: Double = 2.0,
        maxIterations: Int = 30
    ): Shape {
        var current = placedShape
        val fabricBox = fabricPiece.contour.boundingBox()

        for (iteration in 0 until maxIterations) {
            val currentBox = current.boundingBox()

            val leftGap = currentBox.minX - fabricBox.minX
            val rightGap = fabricBox.maxX - currentBox.maxX
            val bottomGap = currentBox.minY - fabricBox.minY
            val topGap = fabricBox.maxY - currentBox.maxY

            val horizontalMoves = listOf(
                if (leftGap <= rightGap) Point(-step, 0.0) else Point(step, 0.0),
                if (leftGap <= rightGap) Point(step, 0.0) else Point(-step, 0.0)
            )

            val verticalMoves = listOf(
                if (bottomGap <= topGap) Point(0.0, -step) else Point(0.0, step),
                if (bottomGap <= topGap) Point(0.0, step) else Point(0.0, -step)
            )

            val diagonalMoves = listOf(
                Point(horizontalMoves[0].x, verticalMoves[0].y),
                Point(horizontalMoves[0].x, verticalMoves[1].y),
                Point(horizontalMoves[1].x, verticalMoves[0].y),
                Point(horizontalMoves[1].x, verticalMoves[1].y)
            )

            val moves = horizontalMoves + verticalMoves + diagonalMoves

            var bestShape: Shape? = null
            var bestScore: CandidateScore? = null

            for (move in moves) {
                val moved = current.apply(
                    Transformation(
                        move = move,
                        rotationDegrees = 0.0,
                        mirrorY = false
                    )
                )

                val movedBoundingBox = moved.boundingBox()

                if (!geometry.contains(fabricPiece.contour, moved)) {
                    continue
                }

                var intersectsPlacedPiece = false
                for (i in placedShapes.indices) {
                    if (!movedBoundingBox.intersects(placedBoundingBoxes[i])) {
                        continue
                    }

                    if (geometry.intersects(moved, placedShapes[i])) {
                        intersectsPlacedPiece = true
                        break
                    }
                }

                if (intersectsPlacedPiece) {
                    continue
                }

                val score = calculateCandidateScore(
                    candidateBoundingBox = movedBoundingBox,
                    placedBoundingBoxes = placedBoundingBoxes,
                    fabricBoundingBox = fabricBox
                )

                if (bestScore == null || score < bestScore) {
                    bestShape = moved
                    bestScore = score
                }
            }

            if (bestShape != null) {
                val currentScore = calculateCandidateScore(
                    candidateBoundingBox = currentBox,
                    placedBoundingBoxes = placedBoundingBoxes,
                    fabricBoundingBox = fabricBox
                )

                if (bestScore!! < currentScore) {
                    current = bestShape
                } else {
                    break
                }
            } else {
                break
            }
        }

        return current
    }

    fun orderPatternPieces(pieces: List<PatternPiece>): List<PatternPiece> {
        return pieces.sortedWith(
            compareByDescending<PatternPiece> { it.shape.contour.area() }
                .thenByDescending {
                    val bb = it.shape.boundingBox()
                    max(bb.maxX - bb.minX, bb.maxY - bb.minY)
                }
        )
    }
    private fun precomputeOrientedPieces(
        pieces: List<PatternPiece>,
        rules: PlacementRules
    ): Map<String, List<OrientedPiece>> {
        return pieces.associate { piece ->
            val orientedVersions = rules.rotation.allowedAnglesDeg.map { angle ->
                val orientedShape = piece.shape.apply(
                    Transformation(
                        move = Point(0.0, 0.0),
                        rotationDegrees = angle,
                        mirrorY = false
                    )
                )

                val orientedBoundingBox = orientedShape.boundingBox()
                val anchors = makeAnchors(orientedShape, orientedBoundingBox)

                OrientedPiece(
                    pieceId = piece.id,
                    angle = angle,
                    shape = orientedShape,
                    boundingBox = orientedBoundingBox,
                    anchors = anchors
                )
            }

            piece.id to orientedVersions
        }
    }

    private fun nearestBorderGap(
        candidateBoundingBox: BoundingBox,
        fabricBoundingBox: BoundingBox
    ): Double {
        val leftGap = candidateBoundingBox.minX - fabricBoundingBox.minX
        val rightGap = fabricBoundingBox.maxX - candidateBoundingBox.maxX
        val bottomGap = candidateBoundingBox.minY - fabricBoundingBox.minY
        val topGap = fabricBoundingBox.maxY - candidateBoundingBox.maxY

        return minOf(leftGap, rightGap, bottomGap, topGap)
    }

    private fun nearestPieceGap(
        candidateBoundingBox: BoundingBox,
        placedBoundingBoxes: List<BoundingBox>
    ): Double {
        if (placedBoundingBoxes.isEmpty()) return Double.MAX_VALUE

        var best = Double.MAX_VALUE

        for (box in placedBoundingBoxes) {
            val dx = minOf(
                kotlin.math.abs(candidateBoundingBox.minX - box.maxX),
                kotlin.math.abs(candidateBoundingBox.maxX - box.minX)
            )
            val dy = minOf(
                kotlin.math.abs(candidateBoundingBox.minY - box.maxY),
                kotlin.math.abs(candidateBoundingBox.maxY - box.minY)
            )
            best = minOf(best, dx, dy)
        }

        return best
    }

    private fun contactCount(
        candidateBoundingBox: BoundingBox,
        placedBoundingBoxes: List<BoundingBox>,
        fabricBoundingBox: BoundingBox,
        eps: Double = 3.0
    ): Double {
        var contacts = 0.0

        if (kotlin.math.abs(candidateBoundingBox.minX - fabricBoundingBox.minX) <= eps) contacts += 1.0
        if (kotlin.math.abs(candidateBoundingBox.maxX - fabricBoundingBox.maxX) <= eps) contacts += 1.0
        if (kotlin.math.abs(candidateBoundingBox.minY - fabricBoundingBox.minY) <= eps) contacts += 1.0
        if (kotlin.math.abs(candidateBoundingBox.maxY - fabricBoundingBox.maxY) <= eps) contacts += 1.0

        for (box in placedBoundingBoxes) {
            if (kotlin.math.abs(candidateBoundingBox.minX - box.maxX) <= eps) contacts += 1.0
            if (kotlin.math.abs(candidateBoundingBox.maxX - box.minX) <= eps) contacts += 1.0
            if (kotlin.math.abs(candidateBoundingBox.minY - box.maxY) <= eps) contacts += 1.0
            if (kotlin.math.abs(candidateBoundingBox.maxY - box.minY) <= eps) contacts += 1.0
        }

        return contacts
    }

    private fun sampleEdgePointsSmart(a: Point, b: Point, spacing: Double = 40.0): List<Point> {
        val dx = b.x - a.x
        val dy = b.y - a.y
        val length = kotlin.math.hypot(dx, dy)

        val parts = (length / spacing).toInt().coerceIn(1, 4)

        return (1 until parts).map { i ->
            val t = i.toDouble() / parts.toDouble()
            Point(
                x = a.x + dx * t,
                y = a.y + dy * t
            )
        }
    }

    private fun getTargetPointsSmart(
        fabricShape: Shape,
        placedShapes: List<Shape>
    ): List<Point> {
        val result = mutableListOf<Point>()

        // Fabric vertices
        result += fabricShape.contour.points

        // Adaptive fabric edge samples
        for ((a, b) in fabricShape.contour.edges()) {
            result += sampleEdgePointsSmart(a, b, spacing = 40.0)
        }

        // Compact placed-piece features
        for (shape in placedShapes) {
            val bb = shape.boundingBox()

            result += listOf(
                Point(bb.minX, bb.minY),
                Point(bb.minX, bb.maxY),
                Point(bb.maxX, bb.minY),
                Point(bb.maxX, bb.maxY),
                Point((bb.minX + bb.maxX) / 2.0, bb.minY),
                Point((bb.minX + bb.maxX) / 2.0, bb.maxY),
                Point(bb.minX, (bb.minY + bb.maxY) / 2.0),
                Point(bb.maxX, (bb.minY + bb.maxY) / 2.0)
            )

            val contourPts = shape.contour.points
            val step = (contourPts.size / 4).coerceAtLeast(1)
            result += contourPts.filterIndexed { index, _ -> index % step == 0 }.take(4)
        }

        return result.distinctBy { shortenNumbers(it) }
    }

    private fun candidateKey(candidate: CandidatePlacement): Triple<Long, Long, Long> {
        val cut = 100.0
        val x = kotlin.math.round(candidate.boundingBox.minX * cut).toLong()
        val y = kotlin.math.round(candidate.boundingBox.minY * cut).toLong()
        val a = kotlin.math.round(candidate.transformation.rotationDegrees * 10.0).toLong()
        return Triple(x, y, a)
    }
}


