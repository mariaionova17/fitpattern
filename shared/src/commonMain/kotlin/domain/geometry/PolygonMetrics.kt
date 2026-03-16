package domain.geometry

import kotlin.math.abs

fun Polygon.boundingBox(): BoundingBox{
    val xValues = points.map {it.x}
    val yValues = points.map {it.y}

    return BoundingBox(
        xValues.min(),
        yValues.min(),
        xValues.max(),
        yValues.max()
    )
}

fun Polygon.center(): Point{
    val boundingBox = boundingBox()

    return Point(
        (boundingBox.maxX + boundingBox.minX) / 2,
        (boundingBox.maxY + boundingBox.minY) / 2
    )
}

fun Polygon.area(): Double {  // shoelace theorem
    require(points.size >= 3)
    var sum = 0.0
    for (i in points.indices) {
        val a = points[i]
        val b = points[(i + 1) % points.size]
        sum += a.x * b.y - b.x * a.y
    }
    return abs(sum) / 2.0
}

fun Polygon.signedArea(): Double {  // shoelace theorem
    require(points.size >= 3)
    var sum = 0.0
    for (i in points.indices) {
        val a = points[i]
        val b = points[(i + 1) % points.size]
        sum += a.x * b.y - b.x * a.y
    }
    return sum / 2.0
}