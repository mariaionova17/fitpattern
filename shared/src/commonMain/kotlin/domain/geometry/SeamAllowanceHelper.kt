package domain.geometry

import kotlin.math.abs
import kotlin.math.hypot

fun Shape.setSeamAllowance(distance: Double): Shape{
    if (distance == 0.0)
        return this
    return Shape(contour.addSeamAllowance(distance), holes)
}

private fun shiftEdge(a: Point, b: Point, distance: Double, clockWisePolygon: Boolean): Pair<Point, Point> {
    val directionX = b.x - a.x
    val directionY = b.y - a.y
    val length = hypot(directionX, directionY)  // sqrt(x^2 + y^2)

    val normalizedX = directionX / length
    val normalizedY = directionY / length

    var normalX = normalizedY
    if (clockWisePolygon)
        normalX = -normalizedY

    var normalY = -normalizedX
    if (clockWisePolygon)
        normalY = normalizedX

    val shiftedA = Point(a.x + normalX * distance, a.y + normalY * distance)
    val shiftedB = Point(b.x + normalX * distance, b.y + normalY * distance)

    return shiftedA to shiftedB

}

private fun lineIntersection(p1: Point, p2: Point, q1: Point, q2: Point, eps: Double = 1e-2): Point? {
    val pVertical = abs(p1.x - p2.x) <= eps
    val qVertical = abs(q1.x - q2.x) <= eps

    // both lines are vertical -> no intersection
    if (pVertical && qVertical)
        return null
    if (pVertical){
        val k2 = (q1.y - q2.y) / (q1.x - q2.x)
        val b2 = q1.y - q1.x * k2

        val x = p1.x
        val y = k2 * x + b2
        return Point(x, y)
    }

    if (qVertical) {
        val k1 = (p1.y - p2.y) / (p1.x - p2.x)
        val b1 = p1.y - p1.x * k1

        val x = q1.x
        val y = k1 * x + b1
        return Point(x, y)
    }

    val k1 = (p1.y - p2.y) / (p1.x - p2.x)
    val b1 = p1.y - p1.x * k1    // k1*x + b = y

    val k2 = (q1.y - q2.y) / (q1.x - q2.x)
    val b2 = q1.y - q1.x * k2    // k2*x + b = y

    if (abs(k1 - k2) <= eps)
        return null

    val x = (b2 - b1) / (k1 - k2)
    val y = k1 * x + b1

    return Point(x, y)
}

fun Polygon.addSeamAllowance (distance:Double): Polygon{
    require(points.size >= 3) { "Polygon must have at least 3 points" }

    val clockWisePolygon = signedArea() <= 0.0
    val result = mutableListOf<Point>()

    for (i in points.indices){
        val previous = points[(i - 1 + points.size) % points.size]
        val current = points[i]
        val next = points[(i + 1) % points.size]

        val shiftedPrevious = shiftEdge(previous, current, distance, clockWisePolygon)
        val shiftedNext = shiftEdge(current, next, distance, clockWisePolygon)

        val newPoint = lineIntersection(shiftedPrevious.first, shiftedPrevious.second, shiftedNext.first, shiftedNext.second)

        if (newPoint != null){
            result += newPoint
        }

        //TODO parallel lines (?)
    }
    return Polygon(result)

}