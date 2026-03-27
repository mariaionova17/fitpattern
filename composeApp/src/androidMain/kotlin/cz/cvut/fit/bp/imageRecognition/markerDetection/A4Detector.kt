package cz.cvut.fit.bp.imageRecognition.markerDetection

import android.graphics.Bitmap
import cz.cvut.fit.bp.imageRecognition.CvPoint
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

fun detectA4(bitmap: Bitmap): Marker? {
    val src = Mat()
    Utils.bitmapToMat(bitmap, src)

    val gray = Mat()
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY)

    val blurred = Mat()
    Imgproc.GaussianBlur(gray, blurred, Size(5.0, 5.0), 0.0)

    // Bright regions: A4 should stand out as white
    val mask = Mat()
    Imgproc.threshold(
        blurred,
        mask,
        0.0,
        255.0,
        Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU
    )

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

    if (contours.isEmpty()) return null

    val imageArea = bitmap.width * bitmap.height.toDouble()
    var bestMarker: Marker? = null
    var bestScore = Double.NEGATIVE_INFINITY

    for (contour in contours) {
        val area = Imgproc.contourArea(contour)
        if (area < imageArea * 0.01) continue

        val contour2f = MatOfPoint2f(*contour.toArray())
        val perimeter = Imgproc.arcLength(contour2f, true)

        val approx = MatOfPoint2f()
        Imgproc.approxPolyDP(contour2f, approx, 0.02 * perimeter, true)

        if (approx.total() != 4L) continue

        val approxPoints = approx.toArray()
        val approxAsMatOfPoint = MatOfPoint(*approxPoints)
        if (!Imgproc.isContourConvex(approxAsMatOfPoint)) continue

        val Marker = orderMarker(approxPoints.map { CvPoint(it.x, it.y) })

        val widthTop = distance(Marker.topLeft, Marker.topRight)
        val widthBottom = distance(Marker.bottomLeft, Marker.bottomRight)
        val heightLeft = distance(Marker.topLeft, Marker.bottomLeft)
        val heightRight = distance(Marker.topRight, Marker.bottomRight)

        val width = (widthTop + widthBottom) / 2.0
        val height = (heightLeft + heightRight) / 2.0

        val longSide = max(width, height)
        val shortSide = minOf(width, height)
        if (shortSide <= 0.0) continue

        val ratio = longSide / shortSide
        val a4Ratio = 297.0 / 210.0 // 1.414...
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
            bestMarker = Marker
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

private fun distance(a: CvPoint, b: CvPoint): Double {
    val dx = a.x - b.x
    val dy = a.y - b.y
    return sqrt(dx * dx + dy * dy)
}