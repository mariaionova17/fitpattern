package domain.geometry
import kotlinx.serialization.Serializable

@Serializable
data class Polygon (
    val points: List<Point>
)