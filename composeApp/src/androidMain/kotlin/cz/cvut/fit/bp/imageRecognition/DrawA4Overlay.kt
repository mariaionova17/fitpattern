
package cz.cvut.fit.bp.imageRecognition

import android.graphics.Bitmap
import cz.cvut.fit.bp.imageRecognition.markerDetection.Marker
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

fun drawA4Overlay(bitmap: Bitmap, Marker: Marker): Bitmap {
    val src = Mat()
    Utils.bitmapToMat(bitmap, src)

    val points = listOf(
        Point(Marker.topLeft.x, Marker.topLeft.y),
        Point(Marker.topRight.x, Marker.topRight.y),
        Point(Marker.bottomRight.x, Marker.bottomRight.y),
        Point(Marker.bottomLeft.x, Marker.bottomLeft.y)
    )

    for (i in points.indices) {
        val p1 = points[i]
        val p2 = points[(i + 1) % points.size]

        Imgproc.line(src, p1, p2, Scalar(0.0, 255.0, 255.0, 255.0), 4)
        Imgproc.circle(src, p1, 8, Scalar(255.0, 0.0, 255.0, 255.0), -1)
    }

    val out = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(src, out)
    return out
}