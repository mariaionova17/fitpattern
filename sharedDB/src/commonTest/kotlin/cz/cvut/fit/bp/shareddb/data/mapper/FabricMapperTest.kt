package cz.cvut.fit.bp.shareddb.data.mapper

import cz.cvut.fit.bp.shareddb.data.entity.FabricPieceEntity
import domain.fabric.FabricPiece
import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FabricMapperTest {
    @Test
    fun `FabricPieceEntity converts to domain FabricPiece`() {
        val contour = rectangle(0.0, 0.0, 10.0, 5.0)
        val holes = listOf(rectangle(2.0, 1.0, 3.0, 2.0))

        val entity = FabricPieceEntity(
            id = "fabric-1",
            name = "",
            projectId = "project-1",
            contourJson = PolygonMapper.polygonToJson(contour),
            holesJson = PolygonMapper.polygonsToJson(holes)
        )

        val fabricPiece = entity.toFabricPiece()

        assertEquals("fabric-1", fabricPiece.id)
        assertEquals(Shape(contour = contour, holes = holes), fabricPiece.contour)
    }

    @Test
    fun `FabricPiece converts to entity with serialized contour and holes`() {
        val fabricPiece = FabricPiece(
            id = "fabric-1",
            name = "",
            contour = Shape(
                contour = rectangle(0.0, 0.0, 10.0, 5.0),
                holes = listOf(rectangle(2.0, 1.0, 3.0, 2.0))
            )
        )

        val entity = fabricPiece.toFabricPieceEntity("project-1")

        assertEquals("fabric-1", entity.id)
        assertEquals("project-1", entity.projectId)
        assertEquals(fabricPiece.contour.contour, PolygonMapper.polygonFromJson(entity.contourJson))
        assertEquals(fabricPiece.contour.holes, PolygonMapper.polygonsFromJson(entity.holesJson))
        assertTrue(entity.contourJson.isNotBlank())
        assertTrue(entity.holesJson.isNotBlank())
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