package cz.cvut.fit.bp.imageRecognition.polygonDetection

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

data class MaskResult(
    val darkMaskMat: Mat,
    val lightMaskMat: Mat,
    val darkMaskBitmap: Bitmap
)

fun generateDarkAndLightMasks(bitmap: Bitmap): MaskResult {
    val src = Mat()
    Utils.bitmapToMat(bitmap, src)

    val grayscale = Mat()
    Imgproc.cvtColor(src, grayscale, Imgproc.COLOR_RGBA2GRAY)

    val blurred = Mat()
    Imgproc.GaussianBlur(grayscale, blurred, Size(7.0, 7.0), 0.0)

    val darkMask = Mat()
    Imgproc.threshold(
        blurred,
        darkMask,
        0.0,
        255.0,
        Imgproc.THRESH_BINARY_INV or Imgproc.THRESH_OTSU
    )

    val lightMask = Mat()
    Imgproc.threshold(
        blurred,
        lightMask,
        0.0,
        255.0,
        Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU
    )

    val kernel = Imgproc.getStructuringElement(
        Imgproc.MORPH_ELLIPSE,
        Size(9.0, 9.0)
    )

    Imgproc.morphologyEx(darkMask, darkMask, Imgproc.MORPH_CLOSE, kernel)
    Imgproc.morphologyEx(darkMask, darkMask, Imgproc.MORPH_OPEN, kernel)

    Imgproc.morphologyEx(lightMask, lightMask, Imgproc.MORPH_CLOSE, kernel)
    Imgproc.morphologyEx(lightMask, lightMask, Imgproc.MORPH_OPEN, kernel)

    val darkMaskRgba = Mat()
    Imgproc.cvtColor(darkMask, darkMaskRgba, Imgproc.COLOR_GRAY2RGBA)

    val darkMaskBitmap = Bitmap.createBitmap(
        darkMask.cols(),
        darkMask.rows(),
        Bitmap.Config.ARGB_8888
    )
    Utils.matToBitmap(darkMaskRgba, darkMaskBitmap)

    return MaskResult(
        darkMaskMat = darkMask,
        lightMaskMat = lightMask,
        darkMaskBitmap = darkMaskBitmap
    )
}