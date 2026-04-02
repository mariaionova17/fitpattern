package cz.cvut.fit.bp.imageRecognition

import androidx.compose.ui.geometry.Offset
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import kotlin.math.hypot

fun imageToScreen(
    image: Offset,
    imageWidth: Float,
    imageHeight: Float,
    containerWidth: Float,
    containerHeight: Float
): Offset {
    val scale = minOf(containerWidth / imageWidth, containerHeight / imageHeight)
    val drawnWidth = imageWidth * scale
    val drawnHeight = imageHeight * scale
    val offsetX = (containerWidth - drawnWidth) / 2f
    val offsetY = (containerHeight - drawnHeight) / 2f

    return Offset(
        x = offsetX + image.x * scale,
        y = offsetY + image.y * scale
    )
}

fun screenToImage(
    screen: Offset,
    imageWidth: Float,
    imageHeight: Float,
    containerWidth: Float,
    containerHeight: Float
): Offset {
    val scale = minOf(containerWidth / imageWidth, containerHeight / imageHeight)
    val drawnWidth = imageWidth * scale
    val drawnHeight = imageHeight * scale
    val offsetX = (containerWidth - drawnWidth) / 2f
    val offsetY = (containerHeight - drawnHeight) / 2f

    val x = ((screen.x - offsetX) / scale).coerceIn(0f, imageWidth)
    val y = ((screen.y - offsetY) / scale).coerceIn(0f, imageHeight)

    return Offset(x, y)
}

fun findNearestVertexOnScreen(
    touch: Offset,
    polygon: List<CvPoint>,
    imageWidth: Float,
    imageHeight: Float,
    containerWidth: Float,
    containerHeight: Float,
    maxDistancePx: Float
): Int? {
    var bestIndex: Int? = null
    var bestDistance = Float.MAX_VALUE

    polygon.forEachIndexed { index, point ->
        val screenPoint = imageToScreen(
            image = Offset(point.x.toFloat(), point.y.toFloat()),
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            containerWidth = containerWidth,
            containerHeight = containerHeight
        )

        val d = hypot(
            touch.x - screenPoint.x,
            touch.y - screenPoint.y
        )

        if (d < bestDistance && d <= maxDistancePx) {
            bestDistance = d
            bestIndex = index
        }
    }

    return bestIndex
}