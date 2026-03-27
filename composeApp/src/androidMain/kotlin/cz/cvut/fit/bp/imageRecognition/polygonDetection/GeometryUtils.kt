package cz.cvut.fit.bp.imageRecognition.polygonDetection

import kotlin.math.sqrt

fun distance(a: CvPoint, b: CvPoint): Double {
    val dx = a.x - b.x
    val dy = a.y - b.y
    return sqrt(dx * dx + dy * dy)
}

fun pointToSegmentDistance(point: CvPoint, a: CvPoint, b: CvPoint): Double {
    val abx = b.x - a.x
    val aby = b.y - a.y
    val apx = point.x - a.x
    val apy = point.y - a.y

    val segmentLength = abx * abx + aby * aby
    if (segmentLength == 0.0)
        return distance(point, a)

    val t = ((apx * abx) + (apy * aby)) / segmentLength  // t = 0 projection on a, t = 1 - on b, 0.5 in the middle
    val limitedT = t.coerceIn(0.0, 1.0)  // forces to measure distance between point and a point on the segment

    val closestX = a.x + limitedT * abx
    val closestY = a.y + limitedT * aby

    return distance(CvPoint(point.x, point.y), CvPoint(closestX, closestY))
}

// finds the index of the contour point closest to a given point
fun nearestContourIndex(contour: List<CvPoint>, point: CvPoint): Int {
    return contour.indices.minByOrNull { i -> distance(contour[i], point) } ?: 0
}

fun contourSectionPoints(contour: List<CvPoint>, startIndex: Int, endIndex: Int): List<Pair<Int, CvPoint>> {
    if (contour.isEmpty())
        return emptyList()

    val result = mutableListOf<Pair<Int, CvPoint>>()
    var i = startIndex

    while (true) {
        result.add(i to contour[i])
        if (i == endIndex)
            break
        i = (i + 1) % contour.size
    }
    return result
}