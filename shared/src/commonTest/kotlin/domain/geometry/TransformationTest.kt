package domain.geometry

import kotlin.test.Test
import kotlin.test.assertEquals

class TransformationTest {

    @Test
    fun `rotate handles right angles around origin`() {
        val polygon = Polygon(
            listOf(
                Point(1.0, 2.0),
                Point(-3.0, 4.0)
            )
        )

        assertEquals(
            listOf(Point(-2.0, 1.0), Point(-4.0, -3.0)),
            polygon.rotate(90.0).points
        )
        assertEquals(
            listOf(Point(-1.0, -2.0), Point(3.0, -4.0)),
            polygon.rotate(180.0).points
        )
        assertEquals(
            listOf(Point(2.0, -1.0), Point(4.0, 3.0)),
            polygon.rotate(270.0).points
        )
    }

    @Test
    fun `move mirrorX and mirrorY transform coordinates correctly`() {
        val polygon = Polygon(
            listOf(
                Point(1.0, 2.0),
                Point(-3.0, 4.0)
            )
        )

        assertEquals(
            listOf(Point(6.0, 0.0), Point(2.0, 2.0)),
            polygon.move(Point(5.0, -2.0)).points
        )
        assertEquals(
            listOf(Point(1.0, -2.0), Point(-3.0, -4.0)),
            polygon.mirrorX().points
        )
        assertEquals(
            listOf(Point(-1.0, 2.0), Point(3.0, 4.0)),
            polygon.mirrorY().points
        )
    }

    @Test
    fun `mirrorYAroundCenter flips polygon around bounding box center`() {
        val polygon = Polygon(
            listOf(
                Point(0.0, 0.0),
                Point(4.0, 0.0),
                Point(2.0, 2.0)
            )
        )

        val mirrored = polygon.mirrorYAroundCenter()

        assertEquals(
            listOf(
                Point(4.0, 0.0),
                Point(0.0, 0.0),
                Point(2.0, 2.0)
            ),
            mirrored.points
        )
    }

    @Test
    fun `rotateAroundCenter keeps center and changes bounding box as expected`() {
        val rect = rectangle(0.0, 0.0, 4.0, 2.0)

        val rotated = rect.rotateAroundCenter(90.0)
        val box = rotated.boundingBox()

        assertEquals(1.0, box.minX, 1e-6)
        assertEquals(-1.0, box.minY, 1e-6)
        assertEquals(3.0, box.maxX, 1e-6)
        assertEquals(3.0, box.maxY, 1e-6)
    }

    @Test
    fun `apply performs mirror then rotate then move`() {
        val rect = rectangle(0.0, 0.0, 2.0, 1.0)

        val transformed = rect.apply(
            Transformation(
                move = Point(10.0, 0.0),
                rotationDegrees = 90.0,
                mirrorY = true
            )
        )

        val box = transformed.boundingBox()

        assertEquals(10.5, box.minX, 1e-6)
        assertEquals(-0.5, box.minY, 1e-6)
        assertEquals(11.5, box.maxX, 1e-6)
        assertEquals(1.5, box.maxY, 1e-6)
    }

    @Test
    fun `shape apply transforms holes too`() {
        val shape = Shape(
            contour = rectangle(0.0, 0.0, 4.0, 4.0),
            holes = listOf(rectangle(1.0, 1.0, 2.0, 2.0))
        )

        val moved = shape.apply(Transformation(move = Point(5.0, 0.0)))

        assertEquals(5.0, moved.contour.boundingBox().minX, 1e-9)
        assertEquals(6.0, moved.holes.single().boundingBox().minX, 1e-9)
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