package cz.cvut.fit.bp.imageRecognition.markerDetection

import cz.cvut.fit.bp.imageRecognition.CvPoint
import cz.cvut.fit.bp.imageRecognition.distance
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc

fun computeA4Homography(marker: Marker): Mat {
    val topWidth = distance(marker.topLeft, marker.topRight)
    val rightHeight = distance(marker.topRight, marker.bottomRight)

    val isHorizontal = topWidth > rightHeight

    val targetWidth = if (isHorizontal) 2970.0 else 2100.0
    val targetHeight = if (isHorizontal) 2100.0 else 2970.0

    val srcPts = MatOfPoint2f(
        Point(marker.topLeft.x, marker.topLeft.y),
        Point(marker.topRight.x, marker.topRight.y),
        Point(marker.bottomRight.x, marker.bottomRight.y),
        Point(marker.bottomLeft.x, marker.bottomLeft.y)
    )

    val dstPts = MatOfPoint2f(
        Point(0.0, 0.0),
        Point(targetWidth - 1.0, 0.0),
        Point(targetWidth - 1.0, targetHeight - 1.0),
        Point(0.0, targetHeight - 1.0)
    )

    return Imgproc.getPerspectiveTransform(srcPts, dstPts)
}

fun transformPolygon(points: List<CvPoint>, homography: Mat): List<CvPoint> {
    if (points.isEmpty()) return emptyList()

    val src = MatOfPoint2f(
        *points.map { Point(it.x, it.y) }.toTypedArray()
    )
    val dst = MatOfPoint2f()

    Core.perspectiveTransform(src, dst, homography)

    return dst.toArray().map { CvPoint(it.x, it.y) }
}