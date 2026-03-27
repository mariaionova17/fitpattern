package cz.cvut.fit.bp.imageRecognition

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

fun drawPolygonOverlay(
    bitmap: Bitmap,
    polygon: List<CvPoint>
): Bitmap {
    val src = Mat()
    Utils.bitmapToMat(bitmap, src)

    if (polygon.size >= 2) {
        val points = polygon.map { Point(it.x, it.y) }

        for (i in points.indices) {
            val p1 = points[i]
            val p2 = points[(i + 1) % points.size]

            Imgproc.line(
                src,
                p1,
                p2,
                Scalar(0.0, 255.0, 0.0, 255.0),
                4
            )

            Imgproc.circle(
                src,
                p1,
                8,
                Scalar(255.0, 0.0, 0.0, 255.0),
                -1
            )
        }
    }

    val out = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(src, out)
    return out
}