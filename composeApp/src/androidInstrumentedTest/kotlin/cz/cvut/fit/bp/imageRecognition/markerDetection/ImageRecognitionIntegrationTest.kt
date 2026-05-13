package cz.cvut.fit.bp.imageRecognition.markerDetection

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.opencv.android.OpenCVLoader
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint

@RunWith(AndroidJUnit4::class)
class ImageRecognitionIntegrationTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun loadOpenCv() {
            if (!OpenCVLoader.initLocal()) {
                fail("OpenCV initialization failed")
            }
        }
    }

    @Test
    fun detectA4_returns_fallback_marker_when_bitmap_contains_no_valid_contour() {
        val bitmap = Bitmap.createBitmap(400, 600, Bitmap.Config.ARGB_8888)

        val marker = detectA4(bitmap)

        val width = marker.topRight.x - marker.topLeft.x
        val height = marker.bottomLeft.y - marker.topLeft.y

        assertTrue(width > 0.0)
        assertTrue(height > 0.0)
        assertEquals(marker.topLeft.x, marker.bottomLeft.x, 0.01)
        assertEquals(marker.topRight.x, marker.bottomRight.x, 0.01)
        assertEquals(marker.topLeft.y, marker.topRight.y, 0.01)
        assertEquals(marker.bottomLeft.y, marker.bottomRight.y, 0.01)
    }

    @Test
    fun measurePolygonInMm_returns_expected_measurement_for_a4_rectangle() {
        val rectifiedPolygon = listOf(
            CvPoint(0.0, 0.0),
            CvPoint(2100.0, 0.0),
            CvPoint(2100.0, 2970.0),
            CvPoint(0.0, 2970.0)
        )

        val measurement = measurePolygonInMm(rectifiedPolygon)

        assertEquals(210.0, measurement.widthMm, 0.001)
        assertEquals(297.0, measurement.heightMm, 0.001)
        assertEquals(62370.0, measurement.areaMm2, 0.001)
    }
}