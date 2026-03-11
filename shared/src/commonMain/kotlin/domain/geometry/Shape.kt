package domain.geometry

data class Shape (
    val contour: Polygon,
    val holes: List<Polygon> = emptyList()
)