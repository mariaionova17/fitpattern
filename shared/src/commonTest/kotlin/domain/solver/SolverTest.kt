package domain.solver

import domain.fabric.FabricPiece
import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.layout.SimpleGeometryKernel
import domain.pattern.PatternPiece
import domain.pattern.PlacementRules
import domain.project.Project
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BetterSolverTest {

    private val geometry = SimpleGeometryKernel()
    private val solver = BetterSolver(geometry)

    @Test
    fun `solve throws when there are no fabric pieces`() {
        val project = Project(
            id = "project-1",
            name = "No fabric",
            fabricPieces = emptyList(),
            rules = PlacementRules(),
            seamAllowance = 0.0
        )

        val problem = NestingProblem(
            project = project,
            pieces = listOf(
                patternPiece("piece-1", 10.0, 10.0)
            )
        )

        assertFailsWith<IllegalArgumentException> {
            solver.solve(problem) { }
        }
    }

    @Test
    fun `solve places one rectangular piece into one larger rectangular fabric`() {
        val project = Project(
            id = "project-1",
            name = "Single placement",
            fabricPieces = listOf(
                fabricPiece("fabric-1", 100.0, 100.0)
            ),
            rules = PlacementRules(),
            seamAllowance = 0.0
        )

        val piece = patternPiece("piece-1", 20.0, 10.0)

        val progressValues = mutableListOf<Float>()

        val result = solver.solve(
            NestingProblem(project = project, pieces = listOf(piece))
        ) { progress ->
            progressValues += progress
        }

        assertEquals(1, result.bestLayout.placed.size)
        assertTrue(result.bestLayout.unplaced.isEmpty())

        val placement = result.bestLayout.placed.single()
        assertEquals("piece-1", placement.pieceId)
        assertEquals(0, placement.fabricPieceIndex)

        assertTrue(progressValues.isNotEmpty())
        assertTrue(progressValues.all { it in 0f..1f })
        assertEquals(1f, progressValues.last())
    }

    @Test
    fun `solve marks piece as unplaced when it does not fit`() {
        val project = Project(
            id = "project-1",
            name = "Too small fabric",
            fabricPieces = listOf(
                fabricPiece("fabric-1", 10.0, 10.0)
            ),
            rules = PlacementRules(),
            seamAllowance = 0.0
        )

        val piece = patternPiece("piece-1", 20.0, 20.0)

        val result = solver.solve(
            NestingProblem(project = project, pieces = listOf(piece))
        ) { }

        assertTrue(result.bestLayout.placed.isEmpty())
        assertEquals(listOf("piece-1"), result.bestLayout.unplaced)
    }

    @Test
    fun `solve can place piece into second fabric when first one is too small`() {
        val project = Project(
            id = "project-1",
            name = "Multiple fabrics",
            fabricPieces = listOf(
                fabricPiece("fabric-1", 10.0, 10.0),
                fabricPiece("fabric-2", 100.0, 100.0)
            ),
            rules = PlacementRules(),
            seamAllowance = 0.0
        )

        val piece = patternPiece("piece-1", 20.0, 20.0)

        val result = solver.solve(
            NestingProblem(project = project, pieces = listOf(piece))
        ) { }

        assertEquals(1, result.bestLayout.placed.size)
        assertTrue(result.bestLayout.unplaced.isEmpty())
        assertEquals(1, result.bestLayout.placed.single().fabricPieceIndex)
    }

    @Test
    fun `solve respects seam allowance and can make an otherwise fitting piece unplaced`() {
        val project = Project(
            id = "project-1",
            name = "Seam allowance",
            fabricPieces = listOf(
                fabricPiece("fabric-1", 10.0, 10.0)
            ),
            rules = PlacementRules(),
            seamAllowance = 1.0
        )

        val piece = patternPiece("piece-1", 10.0, 10.0)

        val result = solver.solve(
            NestingProblem(project = project, pieces = listOf(piece))
        ) { }

        assertTrue(result.bestLayout.placed.isEmpty())
        assertEquals(listOf("piece-1"), result.bestLayout.unplaced)
    }

    private fun fabricPiece(id: String, width: Double, height: Double): FabricPiece =
        FabricPiece(
            id = id,
            contour = rectangleShape(width, height)
        )

    private fun patternPiece(id: String, width: Double, height: Double): PatternPiece =
        PatternPiece(
            id = id,
            name = id,
            shape = rectangleShape(width, height)
        )

    private fun rectangleShape(width: Double, height: Double): Shape =
        Shape(
            contour = Polygon(
                listOf(
                    Point(0.0, 0.0),
                    Point(width, 0.0),
                    Point(width, height),
                    Point(0.0, height)
                )
            )
        )
}
