package cz.cvut.fit.bp.shareddb.data.mapper

import cz.cvut.fit.bp.shareddb.data.entity.FabricPieceEntity
import domain.fabric.FabricPiece
import domain.geometry.Shape

fun FabricPieceEntity.toFabricPiece(): FabricPiece {
    return FabricPiece(
        id = id,
        contour = Shape(
            contour = PolygonMapper.polygonFromJson(contourJson),
            holes = PolygonMapper.polygonsFromJson(holesJson)
        )
    )
}

fun FabricPiece.toFabricPieceEntity(projectId: String): FabricPieceEntity {
    return FabricPieceEntity(
        id = id,
        projectId = projectId,
        contourJson = PolygonMapper.polygonToJson(contour.contour),
        holesJson = PolygonMapper.polygonsToJson(contour.holes)
    )
}