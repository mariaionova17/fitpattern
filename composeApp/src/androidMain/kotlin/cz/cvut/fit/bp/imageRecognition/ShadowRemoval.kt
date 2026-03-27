package cz.cvut.fit.bp.imageRecognition

import org.opencv.core.*
import org.opencv.imgproc.Imgproc

fun removeShadows(src: Mat): Mat {

    // Convert to LAB color space
    val rgb = Mat()
    Imgproc.cvtColor(src, rgb, Imgproc.COLOR_RGBA2RGB)

    val lab = Mat()
    Imgproc.cvtColor(rgb, lab, Imgproc.COLOR_RGB2Lab)

    val channels = ArrayList<Mat>()
    Core.split(lab, channels)

    val L = channels[0]
    val A = channels[1]
    val B = channels[2]

    // Strong blur to estimate illumination
    val background = Mat()
    Imgproc.GaussianBlur(
        L,
        background,
        Size(51.0, 51.0),
        0.0
    )

    // Normalize illumination
    val normalized = Mat()
    Core.divide(L, background, normalized, 255.0)

    // Replace lightness channel
    channels[0] = normalized

    val merged = Mat()
    Core.merge(channels, merged)

    val rgbResult = Mat()
    Imgproc.cvtColor(merged, rgbResult, Imgproc.COLOR_Lab2RGB)

    val result = Mat()
    Imgproc.cvtColor(rgbResult, result, Imgproc.COLOR_RGB2RGBA)

    return result
}