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
import cz.cvut.fit.bp.imageRecognition.markerDetection.Marker
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint

@Composable
fun EditableA4Overlay(
    bitmap: Bitmap,
    marker: Marker,
    onMarkerChange: (Marker) -> Unit,
    modifier: Modifier = Modifier
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var activePointIndex by remember { mutableStateOf<Int?>(null) }

    val imageWidth = bitmap.width.toFloat()
    val imageHeight = bitmap.height.toFloat()
    val aspectRatio = imageWidth / imageHeight

    val points = listOf(
        CvPoint(marker.topLeft.x, marker.topLeft.y),
        CvPoint(marker.topRight.x, marker.topRight.y),
        CvPoint(marker.bottomRight.x, marker.bottomRight.y),
        CvPoint(marker.bottomLeft.x, marker.bottomLeft.y)
    )

    val currentPoints by rememberUpdatedState(points)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .onSizeChanged { containerSize = it }
            .pointerInput(containerSize) {
                detectDragGestures(
                    onDragStart = { touchOffset ->
                        activePointIndex = findNearestVertexOnScreen(
                            touch = touchOffset,
                            polygon = currentPoints,
                            imageWidth = imageWidth,
                            imageHeight = imageHeight,
                            containerWidth = containerSize.width.toFloat(),
                            containerHeight = containerSize.height.toFloat(),
                            maxDistancePx = 60f
                        )
                    },
                    onDragEnd = {
                        activePointIndex = null
                    },
                    onDragCancel = {
                        activePointIndex = null
                    },
                    onDrag = { change, _ ->
                        change.consume()

                        val index = activePointIndex ?: return@detectDragGestures

                        val imagePoint = screenToImage(
                            screen = change.position,
                            imageWidth = imageWidth,
                            imageHeight = imageHeight,
                            containerWidth = containerSize.width.toFloat(),
                            containerHeight = containerSize.height.toFloat()
                        )

                        val updatedPoints = currentPoints.toMutableList()
                        updatedPoints[index] = CvPoint(
                            x = imagePoint.x.toDouble(),
                            y = imagePoint.y.toDouble()
                        )

                        val updatedMarker = Marker(
                            topLeft = CvPoint(updatedPoints[0].x, updatedPoints[0].y),
                            topRight = CvPoint(updatedPoints[1].x, updatedPoints[1].y),
                            bottomRight = CvPoint(updatedPoints[2].x, updatedPoints[2].y),
                            bottomLeft = CvPoint(updatedPoints[3].x, updatedPoints[3].y)
                        )

                        onMarkerChange(updatedMarker)
                    }
                )
            }
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Editable A4 overlay image",
            modifier = Modifier.fillMaxSize()
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val screenPoints = points.map {
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
                    color = Color.Yellow,
                    start = p1,
                    end = p2,
                    strokeWidth = 4f
                )
            }

            screenPoints.forEachIndexed { index, point ->
                drawCircle(
                    color = if (index == activePointIndex) Color.Magenta else Color.Red,
                    radius = 12f,
                    center = point
                )
            }
        }
    }
}