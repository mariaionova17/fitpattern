package domain.pattern


data class RotationRules(
    val allowedAnglesDeg: List<Double> = listOf(0.0)
){
    override fun toString(): String {
        return when (allowedAnglesDeg){
            listOf(0.0) -> "not allowed"
            listOf(0.0, 180.0) -> "allowed only vertically"
            listOf(0.0, 90.0, 180.0, 270.0) -> "allowed by 90°"
            else -> "allowed by any angle"
        }
    }
}

enum class GrainConstraint {
    ANY,
    ALONG_WARP,
    ALONG_WEFT
}

data class PlacementRules(
    val rotation: RotationRules = RotationRules(),
    val allowMirror: Boolean = false
)