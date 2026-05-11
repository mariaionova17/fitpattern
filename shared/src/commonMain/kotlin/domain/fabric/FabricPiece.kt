package domain.fabric

import domain.geometry.Shape

data class FabricPiece (
    val id: String,
    val name: String,
    val contour: Shape
)