package domain.fabric

import domain.geometry.Shape

data class FabricPiece (
    val id: String,
    val contour: Shape
)