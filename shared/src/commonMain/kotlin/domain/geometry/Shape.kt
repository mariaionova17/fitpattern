package domain.geometry
import kotlinx.serialization.Serializable

@Serializable
data class Shape (
    val contour: Polygon,
    val holes: List<Polygon> = emptyList()
)