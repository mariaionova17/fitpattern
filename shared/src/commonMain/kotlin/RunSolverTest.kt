import domain.fabric.Fabric
import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.layout.GeometryKernel
import domain.layout.SimpleGeometryKernel
import domain.pattern.PatternPiece
import domain.pattern.PlacementRules
import domain.pattern.RotationRules
import domain.solver.InitialSolver
import domain.solver.NestingProblem
import domain.solver.NestingResult

fun runTestSolver(): Pair<NestingProblem, NestingResult> {
    val geometry: GeometryKernel = SimpleGeometryKernel()

    val fabric = Fabric(
        contour = Shape(
            contour = Polygon(
                listOf(
                    Point(0.0, 0.0),
                    Point(1000.0, 0.0),
                    Point(500.0, 600.0)
                )
            )
        )
    )

    fun rectangle(w: Double, h: Double) =
        Shape(
            Polygon(
                listOf(
                    Point(0.0, 0.0),
                    Point(w, 0.0),
                    Point(w, h),
                    Point(0.0, h)
                )
            )
        )

    /*val pieces = listOf(
        PatternPiece("A", rectangle(200.0, 150.0), PlacementRules(RotationRules(listOf(0.0, 90.0)))),
        PatternPiece("B", rectangle(180.0, 120.0), PlacementRules(RotationRules(listOf(0.0, 90.0)))),
        PatternPiece("C", rectangle(250.0, 200.0), PlacementRules(RotationRules(listOf(0.0, 90.0))))
    )*/

    /*val pieces = listOf(
        PatternPiece("A", rectangle(150.0, 200.0),PlacementRules()),
        PatternPiece("B", rectangle(120.0, 180.0), PlacementRules()),
        PatternPiece("C", rectangle(200.0, 250.0), PlacementRules())
    )*/

    val pieces = listOf(
        PatternPiece("A", Shape(contour = Polygon(listOf(
            Point(0.0, 0.0),
            Point(500.0, 0.0),
            Point(250.0, 200.0)
        ))),PlacementRules(RotationRules(listOf(0.0, 90.0, 180.0)))),
        PatternPiece("B", Shape(contour = Polygon(listOf(
            Point(0.0, 0.0),
            Point(100.0, 0.0),
            Point(50.0, 60.0)
        ))),PlacementRules(RotationRules(listOf(0.0, 90.0, 180.0)))),
        PatternPiece("C", Shape(contour = Polygon(listOf(
            Point(0.0, 0.0),
            Point(100.0, 0.0),
            Point(50.0, 60.0)
        ))),PlacementRules(RotationRules(listOf(0.0, 90.0, 180.0)))),
        PatternPiece("D", Shape(contour = Polygon(listOf(
            Point(0.0, 0.0),
            Point(100.0, 0.0),
            Point(50.0, 60.0)
        ))),PlacementRules(RotationRules(listOf(0.0, 90.0, 180.0)))),
        PatternPiece("E", Shape(contour = Polygon(listOf(
            Point(0.0, 0.0),
            Point(100.0, 0.0),
            Point(50.0, 60.0)
        ))),PlacementRules(RotationRules(listOf(0.0, 90.0, 180.0))))
    )

    val problem = NestingProblem(
        fabrics = listOf(fabric),
        pieces = pieces
    )

    val solver = InitialSolver(geometry)
    val result = solver.solve(problem)

    println("Placed count: ${result.bestLayout.placed.size}")
    println("Unplaced: ${result.bestLayout.unplaced}")
    println("Placements:")

    result.bestLayout.placed.forEach {
        println(
            "${it.pieceId} -> " +
                    "x=${it.transformation.move.x}, " +
                    "y=${it.transformation.move.y}, " +
                    "rot=${it.transformation.rotationDegrees}"
        )
    }

    return problem to result
}