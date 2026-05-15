package domain.geometry

import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.layout.LayoutScore
import domain.layout.SimpleGeometryKernel
import domain.pattern.PatternPiece
import domain.pattern.RotationRules
import domain.solver.BetterSolver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LayoutAndRulesTest {

    @Test
    fun `layout score compares by height then width`() {
        assertTrue(LayoutScore(9.0, 100.0) < LayoutScore(10.0, 1.0))
        assertTrue(LayoutScore(10.0, 1.0) < LayoutScore(10.0, 2.0))
    }

    @Test
    fun `rotation rules string matches supported presets`() {
        assertEquals("not allowed", RotationRules(listOf(0.0)).toString())
        assertEquals("allowed only vertically", RotationRules(listOf(0.0, 180.0)).toString())
        assertEquals("allowed by 90°", RotationRules(listOf(0.0, 90.0, 180.0, 270.0)).toString())
    }

    @Test
    fun `orderPatternPieces sorts by area then longest side`() {
        val solver = BetterSolver(SimpleGeometryKernel())

        val smaller = PatternPiece(
            id = "small",
            name = "small",
            shape = Shape(rectangle(0.0, 0.0, 2.0, 2.0)) // area 4, max side 2
        )
        val mediumWide = PatternPiece(
            id = "mediumWide",
            name = "mediumWide",
            shape = Shape(rectangle(0.0, 0.0, 6.0, 2.0)) // area 12, max side 6
        )
        val mediumNarrow = PatternPiece(
            id = "mediumNarrow",
            name = "mediumNarrow",
            shape = Shape(rectangle(0.0, 0.0, 4.0, 3.0)) // area 12, max side 4
        )

        val ordered = solver.orderPatternPieces(
            listOf(smaller, mediumNarrow, mediumWide)
        )

        assertEquals(
            listOf("mediumWide", "mediumNarrow", "small"),
            ordered.map { it.id }
        )
    }

    private fun rectangle(minX: Double, minY: Double, maxX: Double, maxY: Double) =
        Polygon(
            listOf(
                Point(minX, minY),
                Point(maxX, minY),
                Point(maxX, maxY),
                Point(minX, maxY)
            )
        )
}