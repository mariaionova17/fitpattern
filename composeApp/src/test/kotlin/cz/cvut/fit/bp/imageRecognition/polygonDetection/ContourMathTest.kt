package cz.cvut.fit.bp.imageRecognition.polygonDetection

import kotlin.test.Test
import kotlin.test.assertEquals

class ContourMathTest {

    @Test
    fun `distance returns distance between two points`() {
        val a = CvPoint(0.0, 0.0)
        val b = CvPoint(3.0, 4.0)

        assertEquals(5.0, distance(a, b), 1e-9)
    }

    @Test
    fun `pointToSegmentDistance returns perpendicular distance for projected point on segment`() {
        val point = CvPoint(1.0, 1.0)
        val a = CvPoint(0.0, 0.0)
        val b = CvPoint(2.0, 0.0)

        assertEquals(1.0, pointToSegmentDistance(point, a, b), 1e-9)
    }

    @Test
    fun `pointToSegmentDistance uses nearest endpoint when projection is outside segment`() {
        val point = CvPoint(3.0, 4.0)
        val a = CvPoint(0.0, 0.0)
        val b = CvPoint(1.0, 0.0)

        assertEquals(4.47213595499958, pointToSegmentDistance(point, a, b), 1e-9)
    }

    @Test
    fun `pointToSegmentDistance falls back to point distance when segment has zero length`() {
        val point = CvPoint(4.0, 5.0)
        val a = CvPoint(1.0, 1.0)
        val b = CvPoint(1.0, 1.0)

        assertEquals(5.0, pointToSegmentDistance(point, a, b), 1e-9)
    }

    @Test
    fun `nearestContourIndex returns index of closest contour point`() {
        val contour = listOf(
            CvPoint(0.0, 0.0),
            CvPoint(10.0, 0.0),
            CvPoint(10.0, 10.0),
            CvPoint(0.0, 10.0)
        )

        val point = CvPoint(9.2, 9.1)

        assertEquals(2, nearestContourIndex(contour, point))
    }

    @Test
    fun `nearestContourIndex returns zero for empty contour`() {
        assertEquals(0, nearestContourIndex(emptyList(), CvPoint(1.0, 1.0)))
    }

    @Test
    fun `contourSectionPoints returns inclusive section without wrap`() {
        val contour = listOf(
            CvPoint(0.0, 0.0),
            CvPoint(1.0, 0.0),
            CvPoint(2.0, 0.0),
            CvPoint(3.0, 0.0)
        )

        val section = contourSectionPoints(contour, startIndex = 1, endIndex = 3)

        assertEquals(
            listOf(
                1 to CvPoint(1.0, 0.0),
                2 to CvPoint(2.0, 0.0),
                3 to CvPoint(3.0, 0.0)
            ),
            section
        )
    }

    @Test
    fun `contourSectionPoints wraps around contour and keeps both ends`() {
        val contour = listOf(
            CvPoint(0.0, 0.0),
            CvPoint(1.0, 0.0),
            CvPoint(2.0, 0.0),
            CvPoint(3.0, 0.0),
            CvPoint(4.0, 0.0)
        )

        val section = contourSectionPoints(contour, startIndex = 3, endIndex = 1)

        assertEquals(
            listOf(
                3 to CvPoint(3.0, 0.0),
                4 to CvPoint(4.0, 0.0),
                0 to CvPoint(0.0, 0.0),
                1 to CvPoint(1.0, 0.0)
            ),
            section
        )
    }

    @Test
    fun `contourSectionPoints returns empty list for empty contour`() {
        assertEquals(emptyList(), contourSectionPoints(emptyList(), 0, 0))
    }
}