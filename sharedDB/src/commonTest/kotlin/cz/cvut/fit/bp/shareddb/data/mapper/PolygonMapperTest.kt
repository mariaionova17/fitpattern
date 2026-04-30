package cz.cvut.fit.bp.shareddb.data.mapper

import domain.geometry.Point
import domain.geometry.Polygon
import kotlin.test.Test
import kotlin.test.assertEquals

class PolygonMapperTest {

    @Test
    fun `polygonToJson and polygonFromJson preserve polygon`() {
        val polygon = Polygon(
            listOf(
                Point(0.0, 0.0),
                Point(10.5, 0.0),
                Point(10.5, 7.25),
                Point(0.0, 7.25)
            )
        )

        val json = PolygonMapper.polygonToJson(polygon)
        val restored = PolygonMapper.polygonFromJson(json)

        assertEquals(polygon, restored)
    }

    @Test
    fun `polygonsToJson and polygonsFromJson preserve polygon list`() {
        val polygons = listOf(
            rectangle(0.0, 0.0, 2.0, 2.0),
            rectangle(3.0, 3.0, 5.0, 5.0)
        )

        val json = PolygonMapper.polygonsToJson(polygons)
        val restored = PolygonMapper.polygonsFromJson(json)

        assertEquals(polygons, restored)
    }

    private fun rectangle(minX: Double, minY: Double, maxX: Double, maxY: Double): Polygon =
        Polygon(
            listOf(
                Point(minX, minY),
                Point(maxX, minY),
                Point(maxX, maxY),
                Point(minX, maxY)
            )
        )
}
