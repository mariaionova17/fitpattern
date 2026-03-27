package cz.cvut.fit.bp.imageRecognition

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import kotlin.math.hypot

@Composable
fun EditablePolygonOverlay(
    bitmap: Bitmap,
    polygon: List<CvPoint>,
    onPolygonChange: (List<CvPoint>) -> Unit,
    modifier: Modifier = Modifier
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var activePointIndex by remember { mutableStateOf<Int?>(null) }

    val imageWidth = bitmap.width.toFloat()
    val imageHeight = bitmap.height.toFloat()
    val aspectRatio = imageWidth / imageHeight

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .onSizeChanged { containerSize = it }
            .pointerInput(polygon, containerSize) {
                detectDragGestures(
                    onDragStart = { touchOffset ->
                        activePointIndex = findNearestVertexOnScreen(
                            touch = touchOffset,
                            polygon = polygon,
                            imageWidth = imageWidth,
                            imageHeight = imageHeight,
                            containerWidth = containerSize.width.toFloat(),
                            containerHeight = containerSize.height.toFloat(),
                            maxDistancePx = 40f
                        )
                    },
                    onDragEnd = {
                        activePointIndex = null
                    },
                    onDragCancel = {
                        activePointIndex = null
                    },
                    onDrag = { change, _ ->
                        val index = activePointIndex ?: return@detectDragGestures

                        val imagePoint = screenToImage(
                            screen = change.position,
                            imageWidth = imageWidth,
                            imageHeight = imageHeight,
                            containerWidth = containerSize.width.toFloat(),
                            containerHeight = containerSize.height.toFloat()
                        )

                        val updated = polygon.toMutableList()
                        updated[index] = CvPoint(
                            x = imagePoint.x.toDouble(),
                            y = imagePoint.y.toDouble()
                        )
                        onPolygonChange(updated)
                    }
                )
            }
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Editable polygon image",
            modifier = Modifier.fillMaxSize()
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            if (polygon.isEmpty()) return@Canvas

            val screenPoints = polygon.map {
                imageToScreen(
                    image = Offset(it.x.toFloat(), it.y.toFloat()),
                    imageWidth = imageWidth,
                    imageHeight = imageHeight,
                    containerWidth = size.width,
                    containerHeight = size.height
                )
            }

            for (i in screenPoints.indices) {
                val p1 = screenPoints[i]
                val p2 = screenPoints[(i + 1) % screenPoints.size]

                drawLine(
                    color = Color.Green,
                    start = p1,
                    end = p2,
                    strokeWidth = 4f
                )
            }

            screenPoints.forEachIndexed { index, point ->
                drawCircle(
                    color = if (index == activePointIndex) Color.Yellow else Color.Red,
                    radius = 10f,
                    center = point
                )
            }
        }
    }
}

private fun imageToScreen(
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

private fun screenToImage(
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

private fun findNearestVertexOnScreen(
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