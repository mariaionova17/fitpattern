package cz.cvut.fit.bp.imageRecognition

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun WarpDirectionOverlay(
    bitmap: Bitmap,
    polygon: List<CvPoint>,
    tiltDegrees: Double,
    onTiltChanged: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var isDraggingTip by remember { mutableStateOf(false) }

    val currentPolygon by rememberUpdatedState(polygon)
    val currentTiltDegrees by rememberUpdatedState(tiltDegrees)

    val imageWidth = bitmap.width.toFloat()
    val imageHeight = bitmap.height.toFloat()

    val polygonCenterImage = remember(polygon) {
        polygonCentroidCv(polygon)
    }

    Box(
        modifier = modifier
            .onSizeChanged { containerSize = it }
            .pointerInput(containerSize) {
                detectDragGestures(
                    onDragStart = { touch ->
                        val centerOnScreen = imageToScreen(
                            image = polygonCenterImage,
                            imageWidth = imageWidth,
                            imageHeight = imageHeight,
                            containerWidth = containerSize.width.toFloat(),
                            containerHeight = containerSize.height.toFloat()
                        )

                        val tipOnScreen = computeArrowTipOnScreen(
                            center = centerOnScreen,
                            tiltDegrees = currentTiltDegrees,
                            arrowLengthPx = 120f
                        )

                        val distance = hypot(
                            touch.x - tipOnScreen.x,
                            touch.y - tipOnScreen.y
                        )

                        isDraggingTip = distance <= 60f
                    },
                    onDragEnd = {
                        isDraggingTip = false
                    },
                    onDragCancel = {
                        isDraggingTip = false
                    },
                    onDrag = { change, _ ->
                        change.consume()

                        if (!isDraggingTip) return@detectDragGestures

                        val centerOnScreen = imageToScreen(
                            image = polygonCenterImage,
                            imageWidth = imageWidth,
                            imageHeight = imageHeight,
                            containerWidth = containerSize.width.toFloat(),
                            containerHeight = containerSize.height.toFloat()
                        )

                        val dx = (change.position.x - centerOnScreen.x).toDouble()
                        val dy = (change.position.y - centerOnScreen.y).toDouble()

                        val rawAngleRad = atan2(dy, dx)

                        // vertical up
                        val verticalAngleRad = -PI / 2.0
                        val tiltRad = rawAngleRad - verticalAngleRad

                        val tiltDeg = Math.toDegrees(tiltRad)
                        onTiltChanged(normalizeDegrees(tiltDeg))
                    }
                )
            }
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Warp tilt selection",
            modifier = Modifier.fillMaxSize()
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            if (currentPolygon.isNotEmpty()) {
                val screenPoints = currentPolygon.map {
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

                val centerOnScreen = imageToScreen(
                    image = polygonCenterImage,
                    imageWidth = imageWidth,
                    imageHeight = imageHeight,
                    containerWidth = size.width,
                    containerHeight = size.height
                )

                val tipOnScreen = computeArrowTipOnScreen(
                    center = centerOnScreen,
                    tiltDegrees = currentTiltDegrees,
                    arrowLengthPx = 120f
                )

                drawLine(
                    color = Color.Yellow,
                    start = centerOnScreen,
                    end = tipOnScreen,
                    strokeWidth = 6f
                )

                drawCircle(
                    color = Color.Magenta,
                    radius = 10f,
                    center = centerOnScreen
                )

                drawCircle(
                    color = if (isDraggingTip) Color.Red else Color.Yellow,
                    radius = 12f,
                    center = tipOnScreen
                )

                drawArrowHead(
                    tip = tipOnScreen,
                    tail = centerOnScreen,
                    color = Color.Yellow
                )
            }
        }
    }
}

private fun polygonCentroidCv(points: List<CvPoint>): Offset {
    val avgX = points.map { it.x }.average().toFloat()
    val avgY = points.map { it.y }.average().toFloat()
    return Offset(avgX, avgY)
}

private fun computeArrowTipOnScreen(
    center: Offset,
    tiltDegrees: Double,
    arrowLengthPx: Float
): Offset {
    val angleRad = Math.toRadians(-90.0 + tiltDegrees)

    return Offset(
        x = center.x + (arrowLengthPx * cos(angleRad)).toFloat(),
        y = center.y + (arrowLengthPx * sin(angleRad)).toFloat()
    )
}

private fun DrawScope.drawArrowHead(
    tip: Offset,
    tail: Offset,
    color: Color = Color.Yellow,
    headLengthPx: Float = 28f,
    headAngleDeg: Float = 25f,
    strokeWidth: Float = 6f
) {
    val angle = atan2(
        (tip.y - tail.y).toDouble(),
        (tip.x - tail.x).toDouble()
    )
    val headAngleRad = Math.toRadians(headAngleDeg.toDouble())

    val leftAngle = angle + Math.PI - headAngleRad
    val rightAngle = angle + Math.PI + headAngleRad

    val leftPoint = Offset(
        x = tip.x + (headLengthPx * cos(leftAngle)).toFloat(),
        y = tip.y + (headLengthPx * sin(leftAngle)).toFloat()
    )

    val rightPoint = Offset(
        x = tip.x + (headLengthPx * cos(rightAngle)).toFloat(),
        y = tip.y + (headLengthPx * sin(rightAngle)).toFloat()
    )

    drawLine(
        color = color,
        start = tip,
        end = leftPoint,
        strokeWidth = strokeWidth
    )

    drawLine(
        color = color,
        start = tip,
        end = rightPoint,
        strokeWidth = strokeWidth
    )
}

private fun normalizeDegrees(degrees: Double): Double {
    var d = degrees
    while (d > 180.0) d -= 360.0
    while (d < -180.0) d += 360.0
    return d
}