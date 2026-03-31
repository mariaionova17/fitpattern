package domain.fabric

import domain.geometry.Shape
import domain.pattern.PlacementRules

data class Fabric (
    val id: String,
    val contour: Shape
)