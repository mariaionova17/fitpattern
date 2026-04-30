package domain.geometry

import domain.layout.SimpleGeometryKernel
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SimpleGeometryKernelTest {

    private val kernel = SimpleGeometryKernel()

    @Test
    fun `intersects returns true for overlapping polygons`() {
        val a = rectangle(0.0, 0.0, 10.0, 10.0)
        val b = rectangle(8.0, 8.0, 12.0, 12.0)

        assertTrue(kernel.intersects(a, b))
    }

    @Test
    fun `intersects returns false for polygons only touching at edge`() {
        val a = rectangle(0.0, 0.0, 10.0, 10.0)
        val b = rectangle(10.0, 2.0, 12.0, 4.0)

        assertFalse(kernel.intersects(a, b))
    }

    @Test
    fun `contains returns true when polygon is fully inside`() {
        val container = rectangle(0.0, 0.0, 10.0, 10.0)
        val subject = rectangle(2.0, 2.0, 4.0, 4.0)

        assertTrue(kernel.contains(container, subject))
    }

    @Test
    fun `contains returns false when polygon crosses boundary`() {
        val container = rectangle(0.0, 0.0, 10.0, 10.0)
        val subject = rectangle(8.0, 8.0, 12.0, 12.0)

        assertFalse(kernel.contains(container, subject))
    }

    @Test
    fun `shape contains returns true for subject inside solid area`() {
        val container = Shape(
            contour = rectangle(0.0, 0.0, 10.0, 10.0),
            holes = listOf(rectangle(3.0, 3.0, 7.0, 7.0))
        )
        val subject = Shape(rectangle(1.0, 1.0, 2.0, 2.0))

        assertTrue(kernel.contains(container, subject))
    }

    @Test
    fun `shape contains returns false when subject lies in hole`() {
        val container = Shape(
            contour = rectangle(0.0, 0.0, 10.0, 10.0),
            holes = listOf(rectangle(3.0, 3.0, 7.0, 7.0))
        )
        val subject = Shape(rectangle(4.0, 4.0, 6.0, 6.0))

        assertFalse(kernel.contains(container, subject))
    }

    @Test
    fun `shape intersects returns false when other shape lies only inside hole`() {
        val container = Shape(
            contour = rectangle(0.0, 0.0, 10.0, 10.0),
            holes = listOf(rectangle(3.0, 3.0, 7.0, 7.0))
        )
        val subject = Shape(rectangle(4.0, 4.0, 6.0, 6.0))

        assertFalse(kernel.intersects(container, subject))
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