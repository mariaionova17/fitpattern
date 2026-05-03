package cz.cvut.fit.bp.imageRecognition

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import kotlin.math.min
import kotlin.math.sqrt

@Composable
fun EditablePolygonOverlay(
    bitmap: Bitmap,
    polygon: List<CvPoint>,
    onPolygonChange: (List<CvPoint>) -> Unit,
    modifier: Modifier = Modifier
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var activePointIndex by remember { mutableStateOf<Int?>(null) }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val minScale = 1f
    val maxScale = 5f

    val imageWidth = bitmap.width.toFloat()
    val imageHeight = bitmap.height.toFloat()

    val currentPolygon by rememberUpdatedState(
        clampPolygonToImage(
            polygon = polygon,
            imageWidth = imageWidth,
            imageHeight = imageHeight
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { containerSize = it }
            .pointerInput(containerSize) {
                awaitEachGesture {
                    val downChange = awaitPointerEvent().changes.firstOrNull { it.pressed }
                        ?: return@awaitEachGesture

                    var gestureScale = scale
                    var gestureOffset = offset

                    val fit = computeFitRect(
                        containerWidth = containerSize.width.toFloat(),
                        containerHeight = containerSize.height.toFloat(),
                        imageWidth = imageWidth,
                        imageHeight = imageHeight
                    )

                    val hitRadiusPx = (60f / gestureScale).coerceIn(20f, 60f)

                    var draggedVertexIndex: Int? = findNearestDisplayedVertexInFitRect(
                        touch = downChange.position,
                        polygon = currentPolygon,
                        fit = fit,
                        scale = gestureScale,
                        offset = gestureOffset,
                        maxDistancePx = hitRadiusPx
                    )

                    activePointIndex = draggedVertexIndex

                    while (true) {
                        val event = awaitPointerEvent()
                        val pressed = event.changes.filter { it.pressed }

                        if (pressed.isEmpty()) break

                        when {
                            pressed.size >= 2 -> {
                                draggedVertexIndex = null
                                activePointIndex = null

                                val oldScale = gestureScale
                                val oldCentroid = event.calculateCentroid(useCurrent = false)
                                val pan = event.calculatePan()
                                val zoom = event.calculateZoom()

                                val newScale = (oldScale * zoom).coerceIn(minScale, maxScale)
                                val appliedZoom = newScale / oldScale

                                val newOffset = Offset(
                                    x = pan.x + oldCentroid.x * (1f - appliedZoom) + gestureOffset.x * appliedZoom,
                                    y = pan.y + oldCentroid.y * (1f - appliedZoom) + gestureOffset.y * appliedZoom
                                )

                                gestureScale = newScale
                                gestureOffset = clampOffsetTopLeft(
                                    offset = newOffset,
                                    scale = newScale,
                                    containerWidth = containerSize.width.toFloat(),
                                    containerHeight = containerSize.height.toFloat()
                                )

                                scale = gestureScale
                                offset = gestureOffset

                                pressed.forEach { change ->
                                    if (change.positionChanged()) {
                                        change.consume()
                                    }
                                }
                            }

                            draggedVertexIndex != null -> {
                                val change = pressed.first()
                                val index = draggedVertexIndex!!

                                val delta = change.position - change.previousPosition

                                val dxImage = delta.x / (gestureScale * fit.fitScale)
                                val dyImage = delta.y / (gestureScale * fit.fitScale)

                                val updated = currentPolygon.toMutableList()
                                val oldPoint = updated[index]

                                val newX = (oldPoint.x.toFloat() + dxImage).coerceIn(0f, imageWidth)
                                val newY = (oldPoint.y.toFloat() + dyImage).coerceIn(0f, imageHeight)

                                updated[index] = CvPoint(
                                    x = newX.toDouble(),
                                    y = newY.toDouble()
                                )

                                onPolygonChange(updated)

                                if (change.positionChanged()) {
                                    change.consume()
                                }
                            }

                            else -> {
                                // 1 finger not near a vertex -> do nothing
                            }
                        }
                    }

                    activePointIndex = null
                }
            }
    ) {
        val fit = remember(containerSize, imageWidth, imageHeight) {
            computeFitRect(
                containerWidth = containerSize.width.toFloat(),
                containerHeight = containerSize.height.toFloat(),
                imageWidth = imageWidth,
                imageHeight = imageHeight
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                    transformOrigin = TransformOrigin(0f, 0f)
                )
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Editable polygon image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                if (currentPolygon.isEmpty()) return@Canvas

                val screenPoints = currentPolygon.map {
                    imageToScreenInFitRect(
                        image = Offset(it.x.toFloat(), it.y.toFloat()),
                        fit = fit
                    )
                }

                val visibleStrokeWidth = 4f / scale

                for (i in screenPoints.indices) {
                    val p1 = screenPoints[i]
                    val p2 = screenPoints[(i + 1) % screenPoints.size]

                    drawLine(
                        color = Color.Green,
                        start = p1,
                        end = p2,
                        strokeWidth = visibleStrokeWidth
                    )
                }

                screenPoints.forEachIndexed { index, point ->
                    val visibleRadius = if (index == activePointIndex) 18f / scale else 14f / scale

                    drawCircle(
                        color = if (index == activePointIndex) Color.Yellow else Color.Red,
                        radius = visibleRadius,
                        center = point
                    )
                }
            }
        }
    }
}

private data class FitRect(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float,
    val fitScale: Float
)

private fun computeFitRect(
    containerWidth: Float,
    containerHeight: Float,
    imageWidth: Float,
    imageHeight: Float
): FitRect {
    if (containerWidth <= 0f || containerHeight <= 0f || imageWidth <= 0f || imageHeight <= 0f) {
        return FitRect(0f, 0f, 0f, 0f, 1f)
    }

    val fitScale = min(containerWidth / imageWidth, containerHeight / imageHeight)
    val width = imageWidth * fitScale
    val height = imageHeight * fitScale
    val left = (containerWidth - width) / 2f
    val top = (containerHeight - height) / 2f

    return FitRect(
        left = left,
        top = top,
        width = width,
        height = height,
        fitScale = fitScale
    )
}

private fun imageToScreenInFitRect(
    image: Offset,
    fit: FitRect
): Offset {
    return Offset(
        x = fit.left + image.x * fit.fitScale,
        y = fit.top + image.y * fit.fitScale
    )
}

private fun clampOffsetTopLeft(
    offset: Offset,
    scale: Float,
    containerWidth: Float,
    containerHeight: Float
): Offset {
    if (scale <= 1f) return Offset.Zero

    val minX = containerWidth * (1f - scale)
    val minY = containerHeight * (1f - scale)

    return Offset(
        x = offset.x.coerceIn(minX, 0f),
        y = offset.y.coerceIn(minY, 0f)
    )
}

private fun clampPolygonToImage(
    polygon: List<CvPoint>,
    imageWidth: Float,
    imageHeight: Float
): List<CvPoint> {
    return polygon.map { point ->
        CvPoint(
            x = point.x.coerceIn(0.0, imageWidth.toDouble()),
            y = point.y.coerceIn(0.0, imageHeight.toDouble())
        )
    }
}

private fun findNearestDisplayedVertexInFitRect(
    touch: Offset,
    polygon: List<CvPoint>,
    fit: FitRect,
    scale: Float,
    offset: Offset,
    maxDistancePx: Float
): Int? {
    var bestIndex: Int? = null
    var bestDistance = maxDistancePx

    polygon.forEachIndexed { index, point ->
        val basePoint = imageToScreenInFitRect(
            image = Offset(point.x.toFloat(), point.y.toFloat()),
            fit = fit
        )

        val displayedPoint = Offset(
            x = basePoint.x * scale + offset.x,
            y = basePoint.y * scale + offset.y
        )

        val dx = displayedPoint.x - touch.x
        val dy = displayedPoint.y - touch.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance <= bestDistance) {
            bestDistance = distance
            bestIndex = index
        }
    }

    return bestIndex
}