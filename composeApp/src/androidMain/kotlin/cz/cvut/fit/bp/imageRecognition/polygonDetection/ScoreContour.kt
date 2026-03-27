package cz.cvut.fit.bp.imageRecognition.polygonDetection

import kotlin.math.abs

// function scores polygon approximation of a shape
fun scorePolygon(points: List<CvPoint>, imageWidth: Int, imageHeight: Int): Double {
    if (points.size < 3)
        return Double.NEGATIVE_INFINITY

    val area = polygonArea(points)
    val perimeter = polygonPerimeter(points)

    val minX = points.minOf { it.x }
    val maxX = points.maxOf { it.x }
    val minY = points.minOf { it.y }
    val maxY = points.maxOf { it.y }

    val bboxWidth = maxX - minX
    val bboxHeight = maxY - minY

    // if the polygon is closer than 8 pixels to the border, penalty is added for each side near the border
    val touchesBorderPenalty =
        listOf(minX, minY, imageWidth - maxX, imageHeight - maxY)
            .count { it < 8.0 } * 50000.0

    val tooManyPointsPenalty = if (points.size > 24) 10000.0 else 0.0
    val tooFewPointsPenalty = if (points.size < 4) 15000.0 else 0.0

    // to avoid messy shapes with small area
    val compactnessPenalty =
        if (area > 0.0) (perimeter * perimeter / area) * 40.0 else 100000.0

    val almostFullImagePenalty =
        if (bboxWidth > imageWidth * 0.95 || bboxHeight > imageHeight * 0.95) 150000.0 else 0.0

    return area -
            touchesBorderPenalty -
            tooManyPointsPenalty -
            tooFewPointsPenalty -
            compactnessPenalty -
            almostFullImagePenalty
}

private fun polygonArea(points: List<CvPoint>): Double {
    var sum = 0.0
    for (i in points.indices) {
        val p1 = points[i]
        val p2 = points[(i + 1) % points.size]
        sum += p1.x * p2.y - p2.x * p1.y
    }
    return abs(sum) / 2.0
}

private fun polygonPerimeter(points: List<CvPoint>): Double {
    var sum = 0.0
    for (i in points.indices) {
        val p1 = points[i]
        val p2 = points[(i + 1) % points.size]
        sum += distance(p1, p2)
    }
    return sum
}