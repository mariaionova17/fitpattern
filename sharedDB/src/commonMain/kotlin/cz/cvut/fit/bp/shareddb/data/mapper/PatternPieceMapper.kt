package cz.cvut.fit.bp.shareddb.data.mapper

import cz.cvut.fit.bp.shareddb.data.entity.PatternPieceEntity
import domain.geometry.Shape
import domain.pattern.PatternPiece

fun PatternPieceEntity.toPatternPiece(): PatternPiece {
    return PatternPiece(
        id = id,
        name = name,
        shape = Shape(
            contour = PolygonMapper.polygonFromJson(contourJson),
            holes = PolygonMapper.polygonsFromJson(holesJson)
        )
    )
}

fun PatternPiece.toPatternPieceEntity(patternVariantId: String): PatternPieceEntity {
    return PatternPieceEntity(
        id = id,
        name = name,
        patternVariantId = patternVariantId,
        contourJson = PolygonMapper.polygonToJson(shape.contour),
        holesJson = PolygonMapper.polygonsToJson(shape.holes)
    )
}