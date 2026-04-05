import domain.fabric.FabricPiece
import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.geometry.setSeamAllowance
import domain.layout.GeometryKernel
import domain.layout.SimpleGeometryKernel
import domain.pattern.PatternPiece
import domain.pattern.PlacementRules
import domain.pattern.RotationRules
import domain.project.Project
import domain.solver.InitialSolver
import domain.solver.NestingProblem
import domain.solver.NestingResult

/*fun runTestSolver(): Pair<NestingProblem, NestingResult> {


    val geometry: GeometryKernel = SimpleGeometryKernel()

    val fabricPiece = FabricPiece("1",
        contour = Shape(
            contour = Polygon(
                listOf(
                    Point(20.0, 50.0),
                    Point(600.0, 0.0),
                    Point(500.0, 600.0),
                    Point(700.0, 800.0),
                    Point(200.0, 500.0),

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
        PatternPiece("A", rectangle(100.0, 150.0), PlacementRules(RotationRules(listOf(0.0, 90.0)))),
        PatternPiece("B", rectangle(150.0, 120.0), PlacementRules(RotationRules(listOf(0.0, 90.0)))),
        PatternPiece("C", rectangle(400.0, 300.0), PlacementRules(RotationRules(listOf(0.0, 90.0))))
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
        )))),
        PatternPiece("B", Shape(contour = Polygon(listOf(
            Point(0.0, 0.0),
            Point(100.0, 0.0),
            Point(50.0, 60.0)
        )))),
        PatternPiece("C", Shape(contour = Polygon(listOf(
            Point(0.0, 0.0),
            Point(100.0, 0.0),
            Point(50.0, 60.0)
        )))),
        PatternPiece("D", Shape(contour = Polygon(listOf(
            Point(0.0, 0.0),
            Point(100.0, 0.0),
            Point(50.0, 60.0)
        )))),
        PatternPiece("E", Shape(contour = Polygon(listOf(
            Point(0.0, 0.0),
            Point(100.0, 0.0),
            Point(50.0, 60.0)
        )))),
        PatternPiece("G", rectangle(100.0, 150.0)),
        PatternPiece("H", rectangle(150.0, 120.0)),
        PatternPiece("K", rectangle(40.0, 300.0))

    )
    println("Original:")
    pieces.first().shape.contour.points.forEach { println(it) }

    println("Seam:")
    pieces.first().shape.setSeamAllowance(10.0).contour.points.forEach { println(it) }

    val problem = NestingProblem(
        Project(id = "11", name = "Project", fabricPieces = listOf(fabricPiece), PlacementRules(RotationRules(listOf(0.0, 90.0, 180.0))), seamAllowance = 5.0),
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
}*/