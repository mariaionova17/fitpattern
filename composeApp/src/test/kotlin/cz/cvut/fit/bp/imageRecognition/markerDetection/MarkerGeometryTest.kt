package cz.cvut.fit.bp.imageRecognition.markerDetection

import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import domain.geometry.Point
import domain.geometry.Polygon
import kotlin.test.Test
import kotlin.test.assertEquals

class MarkerGeometryTest {

    @Test
    fun `normalizePolygonToBottomLeft shifts smallest x to zero and flips y to bottom-left origin`() {
        val polygon = Polygon(
            listOf(
                Point(5.0, 10.0),
                Point(7.0, 12.0),
                Point(6.0, 15.0)
            )
        )

        val normalized = normalizePolygonToBottomLeft(polygon)

        assertEquals(
            Polygon(
                listOf(
                    Point(0.0, 5.0),
                    Point(2.0, 3.0),
                    Point(1.0, 0.0)
                )
            ),
            normalized
        )
    }

    @Test
    fun `normalizePolygonToBottomLeft returns empty polygon unchanged`() {
        val polygon = Polygon(emptyList())

        assertEquals(polygon, normalizePolygonToBottomLeft(polygon))
    }

    @Test
    fun `alignPolygonWarpToVertical rotates polygon and normalizes result`() {
        val polygon = Polygon(
            listOf(
                Point(0.0, 0.0),
                Point(2.0, 0.0),
                Point(2.0, 1.0),
                Point(0.0, 1.0)
            )
        )

        val aligned = alignPolygonWarpToVertical(
            polygon = polygon,
            warpAngleFromVertical = 270.0
        )

        assertEquals(
            Polygon(
                listOf(
                    Point(0.0, -0.0),
                    Point(0.0, -2.0),
                    Point(1.0, -2.0),
                    Point(1.0, -0.0)
                )
            ),
            aligned
        )
    }

    @Test
    fun `measurePolygonInMm returns correct width height and area for A4-sized rectangle in pixels`() {
        val rectifiedPolygon = listOf(
            CvPoint(0.0, 0.0),
            CvPoint(2100.0, 0.0),
            CvPoint(2100.0, 2970.0),
            CvPoint(0.0, 2970.0)
        )

        val measurement = measurePolygonInMm(rectifiedPolygon)

        assertEquals(210.0, measurement.widthMm, 1e-9)
        assertEquals(297.0, measurement.heightMm, 1e-9)
        assertEquals(62370.0, measurement.areaMm2, 1e-9)
    }

    @Test
    fun `measurePolygonInMm returns zeros for empty polygon`() {
        val measurement = measurePolygonInMm(emptyList())

        assertEquals(0.0, measurement.widthMm, 1e-9)
        assertEquals(0.0, measurement.heightMm, 1e-9)
        assertEquals(0.0, measurement.areaMm2, 1e-9)
    }
}
