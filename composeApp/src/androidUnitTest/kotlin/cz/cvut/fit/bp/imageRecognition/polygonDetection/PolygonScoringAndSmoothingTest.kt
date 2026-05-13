package cz.cvut.fit.bp.imageRecognition.polygonDetection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PolygonScoringAndSmoothingTest {

    @Test
    fun `scorePolygon returns negative infinity for fewer than three points`() {
        val score = scorePolygon(
            points = listOf(CvPoint(0.0, 0.0), CvPoint(1.0, 1.0)),
            imageWidth = 100,
            imageHeight = 100
        )

        assertEquals(Double.NEGATIVE_INFINITY, score)
    }

    @Test
    fun `scorePolygon prefers centered polygon over border touching polygon`() {
        val centered = rectangle(20.0, 20.0, 80.0, 80.0)
        val touchingBorder = rectangle(0.0, 20.0, 60.0, 80.0)

        val centeredScore = scorePolygon(centered, 100, 100)
        val borderScore = scorePolygon(touchingBorder, 100, 100)

        assertTrue(centeredScore > borderScore)
    }

    @Test
    fun `scorePolygon penalizes almost full image polygon`() {
        val almostFullImage = rectangle(1.0, 1.0, 99.0, 99.0)
        val moderate = rectangle(20.0, 20.0, 80.0, 80.0)

        val almostFullScore = scorePolygon(almostFullImage, 100, 100)
        val moderateScore = scorePolygon(moderate, 100, 100)

        assertTrue(moderateScore > almostFullScore)
    }

    @Test
    fun `scorePolygon penalizes polygons with too few points`() {
        val triangle = listOf(
            CvPoint(20.0, 20.0),
            CvPoint(80.0, 20.0),
            CvPoint(50.0, 80.0)
        )
        val rectangle = rectangle(20.0, 20.0, 80.0, 80.0)

        val triangleScore = scorePolygon(triangle, 100, 100)
        val rectangleScore = scorePolygon(rectangle, 100, 100)

        assertTrue(rectangleScore > triangleScore)
    }

    @Test
    fun `smoothPolygon inserts additional point when approximation error is too large`() {
        val originalContour = listOf(
            CvPoint(0.0, 0.0),
            CvPoint(5.0, 0.0),
            CvPoint(10.0, 0.0),
            CvPoint(10.0, 10.0),
            CvPoint(0.0, 10.0)
        )

        val initialPolygon = listOf(
            CvPoint(0.0, 0.0),
            CvPoint(10.0, 0.0),
            CvPoint(0.0, 10.0)
        )

        val smoothed = smoothPolygon(
            originalContour = originalContour,
            initialPolygon = initialPolygon,
            maxErrorPx = 5.0,
            maxVertices = 5
        )

        assertEquals(4, smoothed.size)
        assertTrue(smoothed.contains(CvPoint(10.0, 10.0)))
    }

    @Test
    fun `smoothPolygon returns original approximation when error threshold is already satisfied`() {
        val originalContour = rectangle(0.0, 0.0, 10.0, 10.0)
        val initialPolygon = rectangle(0.0, 0.0, 10.0, 10.0)

        val smoothed = smoothPolygon(
            originalContour = originalContour,
            initialPolygon = initialPolygon,
            maxErrorPx = 0.5,
            maxVertices = 10
        )

        assertEquals(initialPolygon, smoothed)
    }

    @Test
    fun `smoothPolygon returns initial polygon when input contour is too small`() {
        val initialPolygon = listOf(
            CvPoint(0.0, 0.0),
            CvPoint(10.0, 0.0),
            CvPoint(0.0, 10.0)
        )

        val smoothed = smoothPolygon(
            originalContour = listOf(CvPoint(0.0, 0.0), CvPoint(1.0, 1.0)),
            initialPolygon = initialPolygon
        )

        assertEquals(initialPolygon, smoothed)
    }

    private fun rectangle(minX: Double, minY: Double, maxX: Double, maxY: Double): List<CvPoint> =
        listOf(
            CvPoint(minX, minY),
            CvPoint(maxX, minY),
            CvPoint(maxX, maxY),
            CvPoint(minX, maxY)
        )
}
