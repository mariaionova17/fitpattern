package cz.cvut.fit.bp.imageRecognition.markerDetection

import android.graphics.Bitmap
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import cz.cvut.fit.bp.imageRecognition.polygonDetection.distance
import cz.cvut.fit.bp.imageRecognition.polygonDetection.generateDarkAndLightMasks
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.abs
import kotlin.math.max

fun detectA4(bitmap: Bitmap): Marker {
    println("detectA4: started")
    println("detectA4: bitmap size = ${bitmap.width} x ${bitmap.height}")

    val mask = generateDarkAndLightMasks(bitmap).lightMaskMat
    println("detectA4: light mask generated")

    val kernel = Imgproc.getStructuringElement(
        Imgproc.MORPH_RECT,
        Size(5.0, 5.0)
    )
    Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel)
    println("detectA4: morphology close applied")

    val contours = mutableListOf<MatOfPoint>()
    val hierarchy = Mat()

    Imgproc.findContours(
        mask.clone(),
        contours,
        hierarchy,
        Imgproc.RETR_EXTERNAL,
        Imgproc.CHAIN_APPROX_SIMPLE
    )

    println("detectA4: contours found = ${contours.size}")

    if (contours.isEmpty()) {
        println("detectA4: no contours found, using default marker")
        val fallback = createDefaultA4Marker(bitmap)
        println("detectA4: default marker = $fallback")
        return fallback
    }

    val imageArea = bitmap.width * bitmap.height.toDouble()
    println("detectA4: image area = $imageArea")

    var bestMarker: Marker? = null
    var bestScore = Double.NEGATIVE_INFINITY

    for ((index, contour) in contours.withIndex()) {
        val area = Imgproc.contourArea(contour)
        println("detectA4: contour #$index area = $area")

        if (area < imageArea * 0.01) {
            println("detectA4: contour #$index skipped because area is too small")
            continue
        }

        val contour2f = MatOfPoint2f(*contour.toArray())
        val perimeter = Imgproc.arcLength(contour2f, true)
        println("detectA4: contour #$index perimeter = $perimeter")

        val approx = MatOfPoint2f()
        Imgproc.approxPolyDP(contour2f, approx, 0.02 * perimeter, true)

        println("detectA4: contour #$index approx point count = ${approx.total()}")

        if (approx.total() != 4L) {
            println("detectA4: contour #$index skipped because it is not a quadrilateral")
            continue
        }

        val approxPoints = approx.toArray()
        val approxAsMatOfPoint = MatOfPoint(*approxPoints)

        if (!Imgproc.isContourConvex(approxAsMatOfPoint)) {
            println("detectA4: contour #$index skipped because it is not convex")
            continue
        }

        val marker = orderMarker(approxPoints.map { CvPoint(it.x, it.y) })
        println("detectA4: contour #$index ordered marker = $marker")

        val widthTop = distance(marker.topLeft, marker.topRight)
        val widthBottom = distance(marker.bottomLeft, marker.bottomRight)
        val heightLeft = distance(marker.topLeft, marker.bottomLeft)
        val heightRight = distance(marker.topRight, marker.bottomRight)

        val width = (widthTop + widthBottom) / 2.0
        val height = (heightLeft + heightRight) / 2.0

        val longSide = max(width, height)
        val shortSide = minOf(width, height)

        println("detectA4: contour #$index widthTop = $widthTop")
        println("detectA4: contour #$index widthBottom = $widthBottom")
        println("detectA4: contour #$index heightLeft = $heightLeft")
        println("detectA4: contour #$index heightRight = $heightRight")
        println("detectA4: contour #$index avg width = $width")
        println("detectA4: contour #$index avg height = $height")

        if (shortSide <= 0.0) {
            println("detectA4: contour #$index skipped because short side <= 0")
            continue
        }

        val ratio = longSide / shortSide
        val a4Ratio = 297.0 / 210.0
        val ratioPenalty = abs(ratio - a4Ratio) * 10000.0

        println("detectA4: contour #$index ratio = $ratio")
        println("detectA4: contour #$index target A4 ratio = $a4Ratio")
        println("detectA4: contour #$index ratio penalty = $ratioPenalty")

        val rect = Imgproc.boundingRect(contour)
        val touchesBorder = (
                rect.x <= 2 ||
                        rect.y <= 2 ||
                        rect.x + rect.width >= bitmap.width - 2 ||
                        rect.y + rect.height >= bitmap.height - 2
                )

        if (touchesBorder) {
            println("detectA4: contour #$index skipped because it touches image border")
            continue
        }

        val areaFraction = area / imageArea
        println("detectA4: contour #$index area fraction = $areaFraction")

        if (areaFraction > 0.60) {
            println("detectA4: contour #$index skipped because it is too large relative to image")
            continue
        }

        val score = area - ratioPenalty
        println("detectA4: contour #$index final score = $score")

        if (score > bestScore) {
            println("detectA4: contour #$index is new best candidate")
            bestScore = score
            bestMarker = marker
        }
    }

    if (bestMarker == null) {
        println("detectA4: no valid A4 candidate selected, using default marker")
        val fallback = createDefaultA4Marker(bitmap)
        println("detectA4: default marker = $fallback")
        return fallback
    }

    println("detectA4: final best score = $bestScore")
    println("detectA4: final best marker = $bestMarker")
    return bestMarker
}

private fun orderMarker(points: List<CvPoint>): Marker {
    require(points.size == 4)

    println("orderMarker: raw points = $points")

    val sumSorted = points.sortedBy { it.x + it.y }
    val diffSorted = points.sortedBy { it.y - it.x }

    val topLeft = sumSorted.first()
    val bottomRight = sumSorted.last()
    val topRight = diffSorted.first()
    val bottomLeft = diffSorted.last()

    val marker = Marker(topLeft, topRight, bottomRight, bottomLeft)
    println("orderMarker: ordered marker = $marker")

    return marker
}

private fun createDefaultA4Marker(bitmap: Bitmap): Marker {
    val w = bitmap.width.toDouble()
    val h = bitmap.height.toDouble()

    val a4Width = 210.0
    val a4Height = 297.0
    val a4Ratio = a4Width / a4Height

    val maxWidth = w * 0.35
    val maxHeight = h * 0.35

    val widthByHeight = maxHeight * a4Ratio
    val heightByWidth = maxWidth / a4Ratio

    val rectWidth: Double
    val rectHeight: Double

    if (widthByHeight <= maxWidth) {
        rectWidth = widthByHeight
        rectHeight = maxHeight
    } else {
        rectWidth = maxWidth
        rectHeight = heightByWidth
    }

    val left = (w - rectWidth) / 2.0
    val right = left + rectWidth
    val top = (h - rectHeight) / 2.0
    val bottom = top + rectHeight

    val marker = Marker(
        topLeft = CvPoint(left, top),
        topRight = CvPoint(right, top),
        bottomRight = CvPoint(right, bottom),
        bottomLeft = CvPoint(left, bottom)
    )

    println("createDefaultA4Marker: marker = $marker")
    return marker
}