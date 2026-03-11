package domain.pattern


data class RotationRules(
    val allowedAnglesDeg: List<Double> = listOf(0.0)
)
enum class GrainConstraint {
    ANY,
    ALONG_WARP,
    ALONG_WEFT
}

data class PlacementRules(
    val rotation: RotationRules = RotationRules(),
    val allowMirror: Boolean = false,
    val grainConstraint: GrainConstraint = GrainConstraint.ANY,
    val spacing: Double = 0.0
)