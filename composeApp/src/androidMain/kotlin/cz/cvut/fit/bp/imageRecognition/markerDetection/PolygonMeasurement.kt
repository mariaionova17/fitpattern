package cz.cvut.fit.bp.imageRecognition.markerDetection

import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import kotlin.math.abs

data class PolygonMeasurement(
    val widthMm: Double,
    val heightMm: Double,
    val areaMm2: Double
)

//test function
//TODO delete it
fun measurePolygonInMm(rectifiedPolygon: List<CvPoint>): PolygonMeasurement {
    if (rectifiedPolygon.isEmpty()) {
        return PolygonMeasurement(0.0, 0.0, 0.0)
    }

    val minX = rectifiedPolygon.minOf { it.x }
    val maxX = rectifiedPolygon.maxOf { it.x }
    val minY = rectifiedPolygon.minOf { it.y }
    val maxY = rectifiedPolygon.maxOf { it.y }

    // 2100 px = 210 mm, 2970 px = 297 mm => 10 px = 1 mm
    val widthMm = (maxX - minX) / 10.0
    val heightMm = (maxY - minY) / 10.0
    val areaMm2 = polygonArea(rectifiedPolygon) / 100.0

    return PolygonMeasurement(
        widthMm = widthMm,
        heightMm = heightMm,
        areaMm2 = areaMm2
    )
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