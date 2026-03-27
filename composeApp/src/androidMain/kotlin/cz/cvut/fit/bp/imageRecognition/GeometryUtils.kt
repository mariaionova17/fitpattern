package cz.cvut.fit.bp.imageRecognition

import kotlin.math.sqrt

fun distance(a: CvPoint, b: CvPoint): Double {
    val dx = a.x - b.x
    val dy = a.y - b.y
    return sqrt(dx * dx + dy * dy)
}

fun pointToSegmentDistance(p: CvPoint, a: CvPoint, b: CvPoint): Double {
    val abx = b.x - a.x
    val aby = b.y - a.y
    val apx = p.x - a.x
    val apy = p.y - a.y

    val abLen2 = abx * abx + aby * aby
    if (abLen2 == 0.0) return distance(p, a)

    val t = ((apx * abx) + (apy * aby)) / abLen2
    val clampedT = t.coerceIn(0.0, 1.0)

    val closestX = a.x + clampedT * abx
    val closestY = a.y + clampedT * aby

    val dx = p.x - closestX
    val dy = p.y - closestY
    return sqrt(dx * dx + dy * dy)
}

fun nearestContourIndex(contour: List<CvPoint>, point: CvPoint): Int {
    return contour.indices.minByOrNull { i -> distance(contour[i], point) } ?: 0
}

fun contourArcPoints(
    contour: List<CvPoint>,
    startIndex: Int,
    endIndex: Int
): List<Pair<Int, CvPoint>> {
    if (contour.isEmpty()) return emptyList()

    val result = mutableListOf<Pair<Int, CvPoint>>()
    var i = startIndex

    while (true) {
        result.add(i to contour[i])
        if (i == endIndex) break
        i = (i + 1) % contour.size
    }

    return result
}