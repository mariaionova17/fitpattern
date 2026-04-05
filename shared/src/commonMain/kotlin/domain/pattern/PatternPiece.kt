package domain.pattern

import domain.geometry.Shape

data class PatternPiece (
    val id: String,
    val name: String,
    val shape: Shape,
)