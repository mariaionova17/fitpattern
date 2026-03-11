package domain.layout

import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.geometry.boundingBox
import domain.geometry.edges
import domain.geometry.intersects
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SimpleGeometryKernel (private val eps: Double = 1e-9) : GeometryKernel {
    override fun intersects(a: Polygon, b: Polygon): Boolean = polygonsIntersect(a, b)
    override fun intersects(a: Shape, b: Shape): Boolean = shapesIntersect(a, b)
    override fun contains(container: Polygon, subject: Polygon): Boolean = polygonContainsPolygon(container, subject)
    override fun contains(container: Shape, subject: Shape): Boolean = shapeContainsShape(container, subject)

    // Polygon-Polygon intersection
    private fun polygonsIntersect(a: Polygon, b: Polygon): Boolean {
        require(a.points.size >= 3) { "Polygon A must have >= 3 points" }
        require(b.points.size >= 3) { "Polygon B must have >= 3 points" }

        // Checking if any edge of A intersects any edge of B
        if (anyEdgeIntersects(a, b))
            return true

        // if the whole B is inside of A - check via one point
        if (pointInPolygon(b.points[0], a))
            return true
        // if the whole A is inside of B - check via one point
        if (pointInPolygon(a.points[0], b))
            return true

        return false
    }

    // Shape-Shape intersection
    private fun shapesIntersect(a: Shape, b: Shape): Boolean {
        if (!polygonsIntersect(a.contour, b.contour))
            return false

        // If any point of A contour is inside B shape -> intersection
        if (a.contour.points.any {p -> pointInShape(p, b)})
            return true
        // If any point of B contour is inside A shape -> intersection
        if (b.contour.points.any {p -> pointInShape(p, a)})
            return true

        if (anyEdgeIntersects(a.contour, b.contour))
            return true

        // Holes boundaries intersecting the other's filled area
        for (h in a.holes) {
            if (polygonsIntersect(h, b.contour) && h.points.any {p -> pointInShape(p, b)})
                return true
        }
        for (h in b.holes) {
            if (polygonsIntersect(h, a.contour) && h.points.any {p -> pointInShape(p, a)})
                return true
        }

        return false
    }


    private fun pointInPolygon(point: Point, polygon: Polygon, borderCountsAsInside: Boolean = false): Boolean {
        val points = polygon.points
        require(points.size >= 3) { "Polygon must have >= 3 points" }

        // Check if the point is on the boundary
        if (borderCountsAsInside && isPointOnPolygonBoundary(point, polygon))
            return true

        // if a ray from the point to the right crosses an uneven number of edges -> the point is inside, even -> outside
        var inside = false
        var j = points.lastIndex
        for (i in points.indices) {
            val edgeEnd = points[i]
            val edgeStart = points[j]

            val xIntersection = edgeEnd.x + (point.y - edgeEnd.y) * (edgeStart.x - edgeEnd.x) / (edgeStart.y - edgeEnd.y)

            val intersectsRay = ((edgeEnd.y > point.y) != (edgeStart.y > point.y)) && (point.x < xIntersection)

            if (intersectsRay)
                inside = !inside
            j = i
        }
        return inside
    }

    private fun pointInShape(p: Point, shape: Shape): Boolean {
        // point is inside outer contour or on boundary
        if (!pointInPolygon(p, shape.contour, borderCountsAsInside = true))
            return false
        // point is not inside any hole
        if (shape.holes.any { hole -> pointInPolygon(p, hole, borderCountsAsInside = true) })
            return false
        return true
    }

    private fun isPointOnPolygonBoundary(point: Point, polygon: Polygon): Boolean {
        for ((a, b) in polygon.edges()) {
            if (isPointOnSegment(point, a, b))
                return true
        }
        return false
    }

    private fun polygonContainsPolygon(container: Polygon, subject: Polygon): Boolean {
        require(container.points.size >= 3) { "Container must have >= 3 points" }
        require(subject.points.size >= 3) { "Subject must have >= 3 points" }

        // If any subject edge crosses container boundary
        if (anyEdgeIntersects(container, subject))
            return false

        // All subject vertices must be inside or on the boundary
        return subject.points.all { p -> pointInPolygon(p, container, borderCountsAsInside = true) }
    }

    private fun shapeContainsShape(container: Shape, subject: Shape): Boolean {
        // Subject must be inside container outer contour
        if (!polygonContainsPolygon(container.contour, subject.contour))
            return false

        // Subject must not overlap container holes
        for (hole in container.holes) {
            // subject's vertexes shouldn't be inside a hole
            if (subject.contour.points.any { p -> pointInPolygon(p, hole, true) })
                return false
            // subject's edges should not intersect a hole
            if (anyEdgeIntersects(hole, subject.contour))
                return false
        }
        return true
    }

    private fun anyEdgeIntersects(a: Polygon, b: Polygon): Boolean {
        if (!a.boundingBox().intersects(b.boundingBox()))
            return false
        val aEdges = a.edges()
        val bEdges = b.edges()
        for ((a1, a2) in aEdges) {
            for ((b1, b2) in bEdges) {
                if (segmentsIntersect(a1, a2, b1, b2))
                    return true
            }
        }
        return false
    }


    private fun segmentsIntersect(p1: Point, p2: Point, q1: Point, q2: Point): Boolean {
        val o1 = orientation(p1, p2, q1)
        val o2 = orientation(p1, p2, q2)
        val o3 = orientation(q1, q2, p1)
        val o4 = orientation(q1, q2, p2)

        // segments have an intersection X
        if (o1 * o2 < 0 && o3 * o4 < 0)
            return true

        // one point of the segment is on the other segment
        if (abs(o1) <= eps && isPointOnSegment(q1, p1, p2))
            return true
        if (abs(o2) <= eps && isPointOnSegment(q2, p1, p2))
            return true
        if (abs(o3) <= eps && isPointOnSegment(p1, q1, q2))
            return true
        if (abs(o4) <= eps && isPointOnSegment(p2, q1, q2))
            return true

        return false
    }

    private fun orientation(a: Point, b: Point, c: Point): Double {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)
    }
    private fun isPointOnSegment(point: Point, a: Point, b: Point): Boolean {
        // point is not on the same line as segment
        if (abs(orientation(a, b, point)) > eps)
            return false
        // point is on the same line as segment and is between ab ends
        return point.x <= max(a.x, b.x) + eps &&
                point.x >= min(a.x, b.x) - eps &&
                point.y <= max(a.y, b.y) + eps &&
                point.y >= min(a.y, b.y) - eps
    }
}