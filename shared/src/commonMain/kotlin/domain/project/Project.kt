package domain.project

import domain.fabric.Fabric
import domain.pattern.PlacementRules

data class Project(
    val name: String,
    val fabrics: List<Fabric>,
    val rules: PlacementRules,
    val seamAllowance: Double = 0.0
)