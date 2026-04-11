package domain.solver

import domain.fabric.FabricPiece
import domain.geometry.BoundingBox
import domain.geometry.Point
import domain.geometry.Shape
import domain.geometry.Transformation
import domain.geometry.apply
import domain.geometry.area
import domain.layout.GeometryKernel
import domain.pattern.PatternPiece
import domain.geometry.boundingBox
import domain.geometry.contains
import domain.geometry.edges
import domain.geometry.intersects
import domain.geometry.isOnTheBorderOf
import domain.geometry.movedBy
import domain.geometry.setSeamAllowance
import domain.layout.LayoutScore
import domain.layout.LayoutState
import domain.layout.Placement
import domain.pattern.PlacementRules
import kotlin.math.max
import kotlin.time.TimeSource

class InitialSolver(private val geometry: GeometryKernel, private val maxContourAnchors: Int = 20) : NestingSolver {

    override fun solve(problem: NestingProblem): NestingResult{
        val start = TimeSource.Monotonic.markNow()
        require (problem.project.fabricPieces.isNotEmpty()){"No fabric pieces"}

        //TODO - now FOR ONE FABRIC PIECE ONLY
        val fabric = problem.project.fabricPieces.first()
        val fabricBoundingBox = problem.project.fabricPieces.first().contour.boundingBox()

        val orderedPatternPieces = orderPatternPieces(
            problem.pieces.map { piece ->
                piece.copy(
                    shape = piece.shape.setSeamAllowance(problem.project.seamAllowance)
                )
            }
        )
        val placedShapes = mutableListOf<Shape>()
        val placedBoundingBoxes = mutableListOf<BoundingBox>()
        val placedPlacements = mutableListOf<Placement>()
        val notPlaced = mutableListOf<String>()

        for (piece in orderedPatternPieces){
            val candidatePlacement = findBestPlacement(piece, fabric, problem.project.rules, fabricBoundingBox, placedShapes, placedBoundingBoxes)

            if (candidatePlacement != null){
                placedPlacements += Placement(piece.id, 0, candidatePlacement.transformation)
                placedShapes += candidatePlacement.shape
                placedBoundingBoxes += candidatePlacement.boundingBox
            } else {
                notPlaced += piece.id
            }
        }
        val finalScore = calculateLayoutScore(placedBoundingBoxes)
        val timeMs = start.elapsedNow().inWholeMilliseconds
        return NestingResult(LayoutState(placedPlacements, notPlaced, finalScore), triedLayouts = 1, timeMs = timeMs)
    }




    fun orderPatternPieces (pieces: List<PatternPiece>): List<PatternPiece>{
        return pieces.sortedWith(
            compareByDescending <PatternPiece> {it.shape.contour.area()}
        )
    }

    //NO MIRROR, JUST DEGREES DIFFERENCE
    private fun findBestPlacement(piece: PatternPiece, fabricPiece: FabricPiece, rules: PlacementRules, fabricBoundingBox: BoundingBox, placedShapes: List<Shape>, placedBoundingBoxes: List<BoundingBox>): CandidatePlacement?{

        val targetPoints = getTargetPoints(fabricPiece.contour, placedShapes)
        var bestPlacement: CandidatePlacement? = null

        for (orientation in  rules.rotation.allowedAnglesDeg){
            val orientedShape = piece.shape.apply(
                Transformation(
                    move = Point(0.0, 0.0),
                    rotationDegrees = orientation,
                    mirrorY = false
                )
            )
            val orientedBoundingBox = orientedShape.boundingBox()
            val anchors = makeAnchors(orientedShape, orientedBoundingBox)

            for (anchor in anchors){
                for (target in targetPoints){
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

                    val placedBoundingBox = placedShape.contour.boundingBox()

                    if (!geometry.contains(fabricPiece.contour, placedShape)) {
                        continue
                    }

                    var intersectsPlacedPiece = false

                    for (i in placedShapes.indices){
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
                    val candidateScore = calculateCandidateScore(placedBoundingBox, placedBoundingBoxes, fabricBoundingBox)

                    if (bestPlacement != null && candidateScore >= bestPlacement.score) {
                        continue
                    }

                    val candidateTransformation = Transformation(offset, orientation, false)
                    val candidate = CandidatePlacement(candidateTransformation, placedShape,placedBoundingBox, candidateScore)
                    bestPlacement = candidate
                    println("New BEST placement for piece ${piece.id}")
                    println("Offset: $offset")
                    println("Score: area=${candidateScore.area}, border=${candidateScore.isOnFabricBorder}")
                }
            }
        }
        return bestPlacement
    }

    private data class CandidatePlacement(
        val transformation: Transformation,
        val shape: Shape,
        val boundingBox: BoundingBox,
        val score: CandidateScore
    )

    private class CandidateScore(val area: Double, val maxY: Double, val maxX: Double, val isOnFabricBorder: Int): Comparable<CandidateScore>{
        override fun compareTo(other: CandidateScore): Int {
            return compareValuesBy(this, other, {it.area}, {it.isOnFabricBorder}, {it.maxY}, {it.maxX})
        }
    }

    private fun getTargetPoints(fabricShape: Shape, placedShapes: List<Shape>): List<Point>{
        val result = mutableListOf<Point>()

        result += fabricShape.contour.points
        result += fabricShape.contour.edges().flatMap { (a, b) ->
            listOf(
                Point(
                    x = a.x + (b.x - a.x) * 0.25,
                    y = a.y + (b.y - a.y) * 0.25
                ),
                Point(
                    x = a.x + (b.x - a.x) * 0.5,
                    y = a.y + (b.y - a.y) * 0.5
                ),
                Point(
                    x = a.x + (b.x - a.x) * 0.75,
                    y = a.y + (b.y - a.y) * 0.75
                )
            )
        }

        for (shape in placedShapes)
            result += shape.contour.points

        return result.distinctBy {shortenNumbers(it)}
    }

    //points are considered equal if they have less of a 0.1 difference
    private fun shortenNumbers(point: Point, cutSize: Int = 100): Pair<Long,Long> =
        Pair(kotlin.math.round(point.x * cutSize).toLong(), kotlin.math.round(point.y * cutSize).toLong())

    private fun calculateLayoutScore(boundingBoxes: List<BoundingBox>): LayoutScore{
        if (boundingBoxes.isEmpty()) return LayoutScore(0.0, 0.0)
        val minX = boundingBoxes.minOf { it.minX }
        val minY = boundingBoxes.minOf { it.minY }
        val maxX = boundingBoxes.maxOf { it.maxX }
        val maxY = boundingBoxes.maxOf { it.maxY }

        val width = maxX - minX
        val height = maxY - minY

        return LayoutScore(height, width)
    }

    private fun makeAnchors(shape: Shape, boundingBox: BoundingBox): List<Point>{
        val boundingBoxPoints = listOf(
            Point(boundingBox.minX, boundingBox.minY),
            Point(boundingBox.minX, boundingBox.maxY),
            Point(boundingBox.maxX, boundingBox.minY),
            Point(boundingBox.maxX, boundingBox.maxY)
        )

        val contourPoints = shape.contour.points
        val sampledContourPoints =
            if (contourPoints.size <= maxContourAnchors){
                contourPoints
            } else {
                val step = max (1, contourPoints.size / maxContourAnchors)
                contourPoints.filterIndexed { index, _ -> index % step == 0 }.take(maxContourAnchors)
            }

        return (boundingBoxPoints + sampledContourPoints).distinctBy { shortenNumbers(it) }
    }

    private fun calculateCandidateScore(candidateBoundingBox: BoundingBox, placedBoundingBoxes: List<BoundingBox>, fabricBoundingBox: BoundingBox): CandidateScore{
        val allBoxes = placedBoundingBoxes + candidateBoundingBox

        val minX = allBoxes.minOf { it.minX }
        val minY = allBoxes.minOf { it.minY }
        val maxX = allBoxes.maxOf { it.maxX }
        val maxY = allBoxes.maxOf { it.maxY }

        val width = maxX - minX
        val height = maxY - minY
        val BoundedArea = width * height

        var isOnFabricBorder = 1

        if (candidateBoundingBox.isOnTheBorderOf(fabricBoundingBox))
            isOnFabricBorder = 0

        return CandidateScore(
            area = BoundedArea,
            maxY = maxY,
            maxX = maxX,
            isOnFabricBorder = isOnFabricBorder
        )
    }

}