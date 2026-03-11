package domain.geometry

fun Polygon.edges(): List<Pair<Point, Point>> {
    require(points.size >= 2) { "Polygon must have at least 2 points" }

    return points.indices.map { i ->
        val start = points[i]
        val end = points[(i + 1) % points.size]
        start to end
    }
}