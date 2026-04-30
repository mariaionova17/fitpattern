package domain.geometry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PolygonMetricsTest {

    @Test
    fun `edges returns all edges including closing edge`() {
        val polygon = polygon(
            p(0.0, 0.0),
            p(2.0, 0.0),
            p(2.0, 1.0)
        )

        val edges = polygon.edges()

        assertEquals(3, edges.size)
        assertEquals(p(0.0, 0.0) to p(2.0, 0.0), edges[0])
        assertEquals(p(2.0, 0.0) to p(2.0, 1.0), edges[1])
        assertEquals(p(2.0, 1.0) to p(0.0, 0.0), edges[2])
    }

    @Test
    fun `edges requires at least two points`() {
        val polygon = Polygon(listOf(p(0.0, 0.0)))

        assertFailsWith<IllegalArgumentException> {
            polygon.edges()
        }
    }

    @Test
    fun `boundingBox center area and signedArea are computed correctly`() {
        val rect = rectangle(0.0, 0.0, 4.0, 3.0)

        val box = rect.boundingBox()
        val center = rect.center()

        assertEquals(0.0, box.minX, 1e-9)
        assertEquals(0.0, box.minY, 1e-9)
        assertEquals(4.0, box.maxX, 1e-9)
        assertEquals(3.0, box.maxY, 1e-9)

        assertEquals(2.0, center.x, 1e-9)
        assertEquals(1.5, center.y, 1e-9)

        assertEquals(12.0, rect.area(), 1e-9)
        assertEquals(12.0, rect.signedArea(), 1e-9)
    }

    @Test
    fun `signedArea is negative for clockwise polygon`() {
        val clockwise = polygon(
            p(0.0, 0.0),
            p(0.0, 3.0),
            p(4.0, 3.0),
            p(4.0, 0.0)
        )

        assertTrue(clockwise.signedArea() < 0.0)
    }

    @Test
    fun `addSeamAllowance expands rectangle by given distance`() {
        val rect = rectangle(0.0, 0.0, 10.0, 5.0)

        val expanded = rect.addSeamAllowance(2.0)
        val box = expanded.boundingBox()

        assertEquals(4, expanded.points.size)
        assertEquals(-2.0, box.minX, 1e-6)
        assertEquals(-2.0, box.minY, 1e-6)
        assertEquals(12.0, box.maxX, 1e-6)
        assertEquals(7.0, box.maxY, 1e-6)
    }

    @Test
    fun `addSeamAllowance requires at least three points`() {
        val invalid = Polygon(listOf(p(0.0, 0.0), p(1.0, 1.0)))

        assertFailsWith<IllegalArgumentException> {
            invalid.addSeamAllowance(1.0)
        }
    }

    private fun p(x: Double, y: Double) = Point(x, y)

    private fun polygon(vararg points: Point) = Polygon(points.toList())

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