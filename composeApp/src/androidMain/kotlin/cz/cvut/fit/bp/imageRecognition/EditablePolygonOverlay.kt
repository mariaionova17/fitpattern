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

@Composable
fun EditablePolygonOverlay(
    bitmap: Bitmap,
    polygon: List<CvPoint>,
    onPolygonChange: (List<CvPoint>) -> Unit,
    modifier: Modifier = Modifier
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var activePointIndex by remember { mutableStateOf<Int?>(null) }

    val currentPolygon by rememberUpdatedState(polygon)

    val imageWidth = bitmap.width.toFloat()
    val imageHeight = bitmap.height.toFloat()
    val aspectRatio = imageWidth / imageHeight

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
                            polygon = currentPolygon,
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

                        val updated = currentPolygon.toMutableList()
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
                    radius = 12f,
                    center = point
                )
            }
        }
    }
}