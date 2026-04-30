package domain.geometry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BoundingBoxExtensionTest {

    @Test
    fun `width and height are computed correctly`() {
        val box = BoundingBox(1.0, 2.0, 6.5, 9.0)

        assertEquals(5.5, box.width(), 1e-9)
        assertEquals(7.0, box.height(), 1e-9)
    }

    @Test
    fun `movedBy shifts all coordinates`() {
        val box = BoundingBox(1.0, 2.0, 5.0, 6.0)

        val moved = box.movedBy(Point(3.0, -1.5))

        assertEquals(4.0, moved.minX, 1e-9)
        assertEquals(0.5, moved.minY, 1e-9)
        assertEquals(8.0, moved.maxX, 1e-9)
        assertEquals(4.5, moved.maxY, 1e-9)
    }

    @Test
    fun `contains returns true for fully contained box`() {
        val outer = BoundingBox(0.0, 0.0, 10.0, 10.0)
        val inner = BoundingBox(2.0, 2.0, 8.0, 8.0)

        assertTrue(outer.contains(inner))
    }

    @Test
    fun `contains respects epsilon for tiny overflow`() {
        val outer = BoundingBox(0.0, 0.0, 10.0, 10.0)
        val almostInside = BoundingBox(0.0, 0.0, 10.005, 10.005)

        assertTrue(outer.contains(almostInside))
    }

    @Test
    fun `intersects returns true for overlapping boxes`() {
        val a = BoundingBox(0.0, 0.0, 10.0, 10.0)
        val b = BoundingBox(8.0, 8.0, 12.0, 12.0)

        assertTrue(a.intersects(b))
    }

    @Test
    fun `intersects respects epsilon for very small gap`() {
        val a = BoundingBox(0.0, 0.0, 10.0, 10.0)
        val b = BoundingBox(10.005, 2.0, 12.0, 4.0)

        assertTrue(a.intersects(b))
    }

    @Test
    fun `isOnTheBorderOf returns true when one side matches`() {
        val box = BoundingBox(0.0, 0.0, 4.0, 4.0)
        val other = BoundingBox(0.0, 1.0, 3.0, 3.0)

        assertTrue(box.isOnTheBorderOf(other))
    }

    @Test
    fun `isOnTheBorderOf returns false when no side matches`() {
        val box = BoundingBox(0.0, 0.0, 4.0, 4.0)
        val other = BoundingBox(0.5, 0.5, 3.5, 3.5)

        assertFalse(box.isOnTheBorderOf(other))
    }
}