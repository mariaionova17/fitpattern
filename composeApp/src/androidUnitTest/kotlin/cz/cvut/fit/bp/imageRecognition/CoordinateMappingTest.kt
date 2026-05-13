package cz.cvut.fit.bp.imageRecognition

import androidx.compose.ui.geometry.Offset
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CoordinateMappingTest {

    @Test
    fun `imageToScreen converts image coordinates into screen coordinates`() {
        val imagePoint = Offset(20f, 10f)

        val screenPoint = imageToScreen(
            image = imagePoint,
            imageWidth = 100f,
            imageHeight = 50f,
            containerWidth = 300f,
            containerHeight = 300f
        )

        assertEquals(60f, screenPoint.x, 1e-6f)
        assertEquals(105f, screenPoint.y, 1e-6f)
    }

    @Test
    fun `screenToImage converts screen coordinates back into image coordinates`() {
        val imagePoint = screenToImage(
            screen = Offset(60f, 105f),
            imageWidth = 100f,
            imageHeight = 50f,
            containerWidth = 300f,
            containerHeight = 300f
        )

        assertEquals(20f, imagePoint.x, 1e-6f)
        assertEquals(10f, imagePoint.y, 1e-6f)
    }

    @Test
    fun `screenToImage clamps coordinates outside drawn image area`() {
        val imagePoint = screenToImage(
            screen = Offset(-50f, 500f),
            imageWidth = 100f,
            imageHeight = 50f,
            containerWidth = 300f,
            containerHeight = 300f
        )

        assertEquals(0f, imagePoint.x, 1e-6f)
        assertEquals(50f, imagePoint.y, 1e-6f)
    }

    @Test
    fun `findNearestVertexOnScreen returns index of closest vertex inside max distance`() {
        val polygon = listOf(
            CvPoint(10.0, 10.0),
            CvPoint(90.0, 10.0),
            CvPoint(90.0, 40.0),
            CvPoint(10.0, 40.0)
        )

        val index = findNearestVertexOnScreen(
            touch = Offset(28f, 106f),
            polygon = polygon,
            imageWidth = 100f,
            imageHeight = 50f,
            containerWidth = 300f,
            containerHeight = 300f,
            maxDistancePx = 20f
        )

        assertEquals(0, index)
    }

    @Test
    fun `findNearestVertexOnScreen returns null when no vertex is close enough`() {
        val polygon = listOf(
            CvPoint(10.0, 10.0),
            CvPoint(90.0, 10.0),
            CvPoint(90.0, 40.0),
            CvPoint(10.0, 40.0)
        )

        val index = findNearestVertexOnScreen(
            touch = Offset(150f, 150f),
            polygon = polygon,
            imageWidth = 100f,
            imageHeight = 50f,
            containerWidth = 300f,
            containerHeight = 300f,
            maxDistancePx = 10f
        )

        assertNull(index)
    }
}
