package cz.cvut.fit.bp.imageRecognition.markerDetection

import android.graphics.Bitmap
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import cz.cvut.fit.bp.imageRecognition.polygonDetection.distance
import cz.cvut.fit.bp.imageRecognition.polygonDetection.generateDarkAndLightMasks
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.abs
import kotlin.math.max

fun detectA4(bitmap: Bitmap): Marker? {
    val mask = generateDarkAndLightMasks(bitmap).lightMaskMat

    val kernel = Imgproc.getStructuringElement(
        Imgproc.MORPH_RECT,
        Size(5.0, 5.0)
    )
    Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel)

    val contours = mutableListOf<MatOfPoint>()
    val hierarchy = Mat()

    Imgproc.findContours(
        mask.clone(),
        contours,
        hierarchy,
        Imgproc.RETR_EXTERNAL,
        Imgproc.CHAIN_APPROX_SIMPLE
    )

    if (contours.isEmpty())
        return null

    val imageArea = bitmap.width * bitmap.height.toDouble()
    var bestMarker: Marker? = null
    var bestScore = Double.NEGATIVE_INFINITY

    for (contour in contours) {
        val area = Imgproc.contourArea(contour)
        if (area < imageArea * 0.01)
            continue

        val contour2f = MatOfPoint2f(*contour.toArray())
        val perimeter = Imgproc.arcLength(contour2f, true)

        val approx = MatOfPoint2f()
        Imgproc.approxPolyDP(contour2f, approx, 0.02 * perimeter, true)

        if (approx.total() != 4L)
            continue

        val approxPoints = approx.toArray()
        val approxAsMatOfPoint = MatOfPoint(*approxPoints)
        if (!Imgproc.isContourConvex(approxAsMatOfPoint))
            continue

        val marker = orderMarker(approxPoints.map { CvPoint(it.x, it.y) })

        val widthTop = distance(marker.topLeft, marker.topRight)
        val widthBottom = distance(marker.bottomLeft, marker.bottomRight)
        val heightLeft = distance(marker.topLeft, marker.bottomLeft)
        val heightRight = distance(marker.topRight, marker.bottomRight)

        val width = (widthTop + widthBottom) / 2.0
        val height = (heightLeft + heightRight) / 2.0

        val longSide = max(width, height)
        val shortSide = minOf(width, height)
        if (shortSide <= 0.0)
            continue

        val ratio = longSide / shortSide
        val a4Ratio = 297.0 / 210.0
        val ratioPenalty = abs(ratio - a4Ratio) * 10000.0

        val rect = Imgproc.boundingRect(contour)
        val touchesBorder = (
                rect.x <= 2 ||
                        rect.y <= 2 ||
                        rect.x + rect.width >= bitmap.width - 2 ||
                        rect.y + rect.height >= bitmap.height - 2
                )

        val borderPenalty = if (touchesBorder) 50000.0 else 0.0

        val score = area - ratioPenalty - borderPenalty

        if (score > bestScore) {
            bestScore = score
            bestMarker = marker
        }
    }

    return bestMarker
}

private fun orderMarker(points: List<CvPoint>): Marker {
    require(points.size == 4)

    val sumSorted = points.sortedBy { it.x + it.y }
    val diffSorted = points.sortedBy { it.y - it.x }

    val topLeft = sumSorted.first()
    val bottomRight = sumSorted.last()
    val topRight = diffSorted.first()
    val bottomLeft = diffSorted.last()

    return Marker(topLeft, topRight, bottomRight, bottomLeft)
}