package domain.geometry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ShapeExtensionsTest {

    @Test
    fun `boundingBox delegates to contour boundingBox`() {
        val contour = rectangle(0.0, 0.0, 10.0, 5.0)
        val shape = Shape(contour)

        assertEquals(contour.boundingBox(), shape.boundingBox())
    }

    @Test
    fun `rectangleSize returns size for origin aligned rectangle without holes`() {
        val shape = Shape(rectangle(0.0, 0.0, 10.0, 5.0))

        assertEquals(10.0 to 5.0, shape.rectangleSize())
    }

    @Test
    fun `rectangleSize returns null for non rectangle`() {
        val triangle = Shape(
            Polygon(
                listOf(
                    Point(0.0, 0.0),
                    Point(4.0, 0.0),
                    Point(2.0, 3.0)
                )
            )
        )

        assertNull(triangle.rectangleSize())
    }

    @Test
    fun `rectangleSize returns null when holes are present`() {
        val shape = Shape(
            contour = rectangle(0.0, 0.0, 10.0, 5.0),
            holes = listOf(rectangle(2.0, 1.0, 3.0, 2.0))
        )

        assertNull(shape.rectangleSize())
    }

    @Test
    fun `setSeamAllowance with zero returns unchanged shape`() {
        val shape = Shape(
            contour = rectangle(0.0, 0.0, 10.0, 5.0),
            holes = listOf(rectangle(2.0, 1.0, 3.0, 2.0))
        )

        val result = shape.setSeamAllowance(0.0)

        assertEquals(shape, result)
    }

    @Test
    fun `setSeamAllowance expands contour and keeps holes unchanged`() {
        val hole = rectangle(2.0, 1.0, 3.0, 2.0)
        val shape = Shape(
            contour = rectangle(0.0, 0.0, 10.0, 5.0),
            holes = listOf(hole)
        )

        val expanded = shape.setSeamAllowance(1.0)
        val box = expanded.boundingBox()

        assertEquals(listOf(hole), expanded.holes)
        assertEquals(-1.0, box.minX, 1e-6)
        assertEquals(-1.0, box.minY, 1e-6)
        assertEquals(11.0, box.maxX, 1e-6)
        assertEquals(6.0, box.maxY, 1e-6)
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