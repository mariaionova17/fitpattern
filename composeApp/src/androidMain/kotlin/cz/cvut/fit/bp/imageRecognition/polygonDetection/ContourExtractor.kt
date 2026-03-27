package cz.cvut.fit.bp.imageRecognition.polygonDetection

import org.opencv.core.*
import org.opencv.imgproc.Imgproc

fun extractLargestPolygon(mask: Mat, imageWidth: Int, imageHeight: Int): List<CvPoint>? {
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

    val imageArea = imageWidth * imageHeight.toDouble()

    var bestPolygon: List<CvPoint>? = null
    var bestScore = Double.NEGATIVE_INFINITY

    for (contour in contours) {
        val area = Imgproc.contourArea(contour)
        if (area <= imageArea * 0.01)   // ignore noise
            continue

        val imageContour = Imgproc.boundingRect(contour)

        val touchesLeft = imageContour.x <= 3
        val touchesTop = imageContour.y <= 3
        val touchesRight = imageContour.x + imageContour.width >= imageWidth - 3
        val touchesBottom = imageContour.y + imageContour.height >= imageHeight - 3

        val touchedSides = listOf(
            touchesLeft,
            touchesTop,
            touchesRight,
            touchesBottom
        ).count { it }

        // Reject contours that behave like image border or background
        if (touchedSides >= 2)
            continue

        val originalContour = contour.toArray().map { CvPoint(it.x, it.y) }

        val contour2f = MatOfPoint2f(*contour.toArray())
        val perimeter = Imgproc.arcLength(contour2f, true)

        // simplify contour
        val approx = MatOfPoint2f()
        Imgproc.approxPolyDP(contour2f, approx, 0.015 * perimeter, true)

        val initialPolygon = approx.toArray().map { CvPoint(it.x, it.y) }

        val smoothedPolygon = smoothPolygon(
            originalContour = originalContour,
            initialPolygon = initialPolygon,
            maxErrorPx = 10.0,
            maxVertices = 22   //TODO decide how many
        )

        val score = scorePolygon(smoothedPolygon, imageWidth, imageHeight)

        if (score > bestScore) {
            bestScore = score
            bestPolygon = smoothedPolygon
        }
    }

    return bestPolygon
}