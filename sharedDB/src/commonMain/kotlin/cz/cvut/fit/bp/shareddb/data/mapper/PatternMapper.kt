package cz.cvut.fit.bp.shareddb.data.mapper

import cz.cvut.fit.bp.shareddb.data.entity.PatternEntity
import domain.pattern.Pattern
import domain.pattern.PatternVariant

fun PatternEntity.toPattern(
    variants: List<PatternVariant>
): Pattern {
    return Pattern(
        id = id,
        name = name,
        variants = variants,
        pictureUri = pictureUri
    )
}