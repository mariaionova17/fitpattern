package cz.cvut.fit.bp.shareddb.data.mapper

import cz.cvut.fit.bp.shareddb.data.entity.PatternVariantEntity
import domain.pattern.PatternVariant

fun PatternVariantEntity.toPatternVariant(
    pieces: List<cz.cvut.fit.bp.shareddb.data.entity.PatternPieceEntity>
): PatternVariant {
    return PatternVariant(
        id = variantId,
        size = size,
        pieces = pieces.map { it.toPatternPiece() }
    )
}
