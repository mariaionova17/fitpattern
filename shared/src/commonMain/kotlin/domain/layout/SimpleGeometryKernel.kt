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

//TODO fix approximation
class SimpleGeometryKernel (private val eps: Double = 1e-2) : GeometryKernel {
    override fun intersects(a: Polygon, b: Polygon): Boolean = polygonsOverlapAllowTouch(a, b)
    override fun intersects(a: Shape, b: Shape): Boolean = shapesOverlapAllowTouch(a, b)
    override fun contains(container: Polygon, subject: Polygon): Boolean = polygonContainsPolygon(container, subject)
    override fun contains(container: Shape, subject: Shape): Boolean = shapeContainsShape(container, subject)

    // Polygon-Polygon intersection
    // polygon overlaps with another polygon -> true, just touches or doesn't interact -> false
    private fun polygonsOverlapAllowTouch(a: Polygon, b: Polygon): Boolean {
        require(a.points.size >= 3) { "Polygon A must have >= 3 points" }
        require(b.points.size >= 3) { "Polygon B must have >= 3 points" }

        // Checking if any edge of A intersects any edge of B
        if (anyEdgeIntersects(a, b))
            return true

        // Check if the whole B is inside of A or if the whole A is inside of B
        if (b.points.all { pointInPolygon(it, a, borderCountsAsInside = true) }) {
            return true
        }
        if (a.points.all { pointInPolygon(it, b, borderCountsAsInside = true) }) {
            return true
        }

        return false
    }

    // Shape-Shape intersection
    private fun shapesOverlapAllowTouch(a: Shape, b: Shape): Boolean {
        if (!polygonsOverlapAllowTouch(a.contour, b.contour)) { // if polygons don't overlap -> shapes won't be
            return false
        }
        // contours overlap (not touching)

        // one polygon is inside other's hole
        if (a.holes.any { hole -> b.contour.points.all { p -> pointInPolygon(p, hole, borderCountsAsInside = true) } })
            return false
        if (b.holes.any { hole -> a.contour.points.all { p -> pointInPolygon(p, hole, borderCountsAsInside = true) } })
            return false

        return true
    }

    private fun pointStrictlyInShape(p: Point, shape: Shape): Boolean {
        if (!pointInPolygon(p, shape.contour, borderCountsAsInside = false))
            return false
        if (shape.holes.any { hole -> pointInPolygon(p, hole, borderCountsAsInside = true) })
            return false
        return true
    }

    private fun pointInPolygon(point: Point, polygon: Polygon, borderCountsAsInside: Boolean = false): Boolean {
        val points = polygon.points
        require(points.size >= 3) { "Polygon must have >= 3 points" }

        // Check if the point is on the boundary
        if (isPointOnPolygonBoundary(point, polygon))
            return borderCountsAsInside

        // if a ray from the point to the right crosses an uneven number of edges -> the point is inside, even -> outside
        var inside = false
        var j = points.lastIndex
        for (i in points.indices) {
            val edgeEnd = points[i]
            val edgeStart = points[j]

            // if the edge is horizontal -> skip
            val diff = edgeEnd.y - edgeStart.y
            if (abs(diff) <= eps) {
                j = i
                continue
            }

            val xIntersection = edgeEnd.x + (point.y - edgeEnd.y) * (edgeStart.x - edgeEnd.x) / (edgeStart.y - edgeEnd.y)

            if (abs(point.x - xIntersection) <= eps)
                return borderCountsAsInside

            val intersectsRay = ((edgeEnd.y > point.y + eps) != (edgeStart.y > point.y + eps)) && (point.x < xIntersection - eps)

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
            if (isPointOnSegment(point, a, b)) {
                return true
            }
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
            if (anyEdgeIntersectsOrTouching(hole, subject.contour))
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

    private fun anyEdgeIntersectsOrTouching(a: Polygon, b: Polygon): Boolean {
        if (!a.boundingBox().intersects(b.boundingBox()))
            return false
        val aEdges = a.edges()
        val bEdges = b.edges()
        for ((a1, a2) in aEdges) {
            for ((b1, b2) in bEdges) {
                if (segmentsIntersectOrTouching(a1, a2, b1, b2))
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


        // segments have an intersection
        val intersection =
            ((o1 > eps && o2 < -eps) || (o1 < -eps && o2 > eps)) && ((o3 > eps && o4 < -eps) || (o3 < -eps && o4 > eps))

        return intersection
    }

    private fun segmentsIntersectOrTouching(p1: Point, p2: Point, q1: Point, q2: Point): Boolean {
        val o1 = orientation(p1, p2, q1)
        val o2 = orientation(p1, p2, q2)
        val o3 = orientation(q1, q2, p1)
        val o4 = orientation(q1, q2, p2)

        // segments have an intersection
        if (((o1 > eps && o2 < -eps) || (o1 < -eps && o2 > eps)) &&
            ((o3 > eps && o4 < -eps) || (o3 < -eps && o4 > eps)))
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
        if (abs(orientation(a, b, point)) > eps * 0.1)
            return false
        // point is on the same line as segment and is between ab ends
        return point.x <= max(a.x, b.x) + eps &&
                point.x >= min(a.x, b.x) - eps &&
                point.y <= max(a.y, b.y) + eps &&
                point.y >= min(a.y, b.y) - eps
    }
}