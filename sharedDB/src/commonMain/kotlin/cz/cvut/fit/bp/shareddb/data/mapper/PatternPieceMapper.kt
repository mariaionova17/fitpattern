package cz.cvut.fit.bp.shareddb.data.mapper

import cz.cvut.fit.bp.shareddb.data.entity.PatternPieceEntity
import domain.geometry.Shape
import domain.pattern.PatternPiece

fun PatternPieceEntity.toPatternPiece(): PatternPiece {
    return PatternPiece(
        id = id,
        shape = Shape(
            contour = PolygonMapper.polygonFromJson(contourJson),
            holes = PolygonMapper.polygonsFromJson(holesJson)
        )
    )
}

fun PatternPiece.toPatternPieceEntity(patternId: String): PatternPieceEntity {
    return PatternPieceEntity(
        id = id,
        patternId = patternId,
        contourJson = PolygonMapper.polygonToJson(shape.contour),
        holesJson = PolygonMapper.polygonsToJson(shape.holes)
    )
}