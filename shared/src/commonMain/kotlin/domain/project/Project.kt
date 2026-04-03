package domain.project

import domain.fabric.FabricPiece
import domain.pattern.PlacementRules

data class Project(
    val id: String,
    val name: String,
    val fabricPieces: List<FabricPiece>,
    val rules: PlacementRules,
    val seamAllowance: Double = 0.0
)