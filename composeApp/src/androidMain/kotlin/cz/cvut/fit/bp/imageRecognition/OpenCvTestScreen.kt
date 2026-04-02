package cz.cvut.fit.bp.imageRecognition

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cz.cvut.fit.bp.imageRecognition.markerDetection.computeA4Homography
import cz.cvut.fit.bp.imageRecognition.markerDetection.detectA4
import cz.cvut.fit.bp.imageRecognition.markerDetection.editablePolygonToMmPolygon
import cz.cvut.fit.bp.imageRecognition.markerDetection.measurePolygonInMm
import cz.cvut.fit.bp.imageRecognition.markerDetection.transformPolygon
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import cz.cvut.fit.bp.imageRecognition.polygonDetection.detectBestPolygon
import cz.cvut.fit.bp.imageRecognition.polygonDetection.generateDarkAndLightMasks
import domain.geometry.Polygon
import kotlinx.coroutines.launch

@Composable
fun OpenCvTestScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var editablePolygon by remember { mutableStateOf<List<CvPoint>>(emptyList()) }
    var bestOverlayBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var a4OverlayBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var resultPolygon by remember { mutableStateOf<Polygon?>(null) }
    var measurementText by remember { mutableStateOf("") }
    var polygonPointsText by remember { mutableStateOf("") }
    var darkMaskBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var foregroundMaskBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var statusText by remember { mutableStateOf("Press the button to test OpenCV") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {
                scope.launch {
                    try {
                        statusText = "Processing..."

                        System.loadLibrary("opencv_java4")

                        val original = loadBitmapFromAssets(context, "test_image.jpg")

                        val dualResult = detectBestPolygon(original)

                        val masks = generateDarkAndLightMasks(original)
                        darkMaskBitmap = masks.darkMaskBitmap

                        bestOverlayBitmap = dualResult.bestCandidate?.let {
                            drawPolygonOverlay(original, it.polygon)
                        }
                        editablePolygon = dualResult.bestCandidate?.polygon ?: emptyList()

                        val a4 = detectA4(original)
                        a4OverlayBitmap = a4?.let { drawA4Overlay(original, it) }

                        val marker = detectA4(original)
                        val result = detectBestPolygon(original)

                        originalBitmap = original

                        editablePolygon = result.bestCandidate?.polygon ?: emptyList()

                        measurementText = if (marker != null && editablePolygon.isNotEmpty()) {
                            val homography = computeA4Homography(marker)
                            val rectifiedPolygon = transformPolygon(editablePolygon, homography)
                            val measurement = measurePolygonInMm(rectifiedPolygon)

                            resultPolygon = editablePolygonToMmPolygon(editablePolygon, marker)

                            polygonPointsText = buildString {
                                appendLine("Result polygon points (mm):")
                                resultPolygon?.points?.forEachIndexed { index, point ->
                                    appendLine(
                                        "${index + 1}: x=${"%.2f".format(point.x)}, y=${
                                            "%.2f".format(
                                                point.y
                                            )
                                        }"
                                    )
                                }
                            }

                            """
                        Width: ${"%.1f".format(measurement.widthMm)} mm
                        Height: ${"%.1f".format(measurement.heightMm)} mm
                        Area: ${"%.1f".format(measurement.areaMm2)} mm²
                        """.trimIndent()
                        } else {
                            "Measurement unavailable"
                        }
                        statusText = buildString {
                            appendLine("OpenCV test finished")
                            appendLine("Dark candidate: ${dualResult.darkCandidate != null}")
                            appendLine("Light candidate: ${dualResult.lightCandidate != null}")

                            dualResult.bestCandidate?.let { best ->
                                appendLine("Winner: ${best.name}")
                                appendLine("Polygon points: ${best.polygon.size}")
                                appendLine("Score: ${"%.2f".format(best.score)}")
                            } ?: appendLine("No valid polygon detected")
                        }
                    } catch (e: Throwable) {
                        statusText = "Error: ${e.message}"
                        e.printStackTrace()
                    }
                }
            }
        ) {
            Text("Run OpenCV test")
        }

        Text(statusText)

        originalBitmap?.let {
            Text("Original")
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Original image",
                modifier = Modifier.fillMaxWidth()
            )
        }
        darkMaskBitmap?.let {
            Text("Dark Mask")
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Dark mask image",
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (originalBitmap != null && editablePolygon.isNotEmpty()) {
            Text("Editable Polygon")
            EditablePolygonOverlay(
                bitmap = originalBitmap!!,
                polygon = editablePolygon,
                onPolygonChange = { updated ->
                    editablePolygon = updated
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        a4OverlayBitmap?.let {
            Text("A4 Overlay")
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "A4 overlay image",
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text(measurementText)
        if (polygonPointsText.isNotBlank()) {
            Text(polygonPointsText)
        }

        if (resultPolygon != null) {
            Text("Result Polygon Only")
            ResultPolygonPreview(
                polygon = resultPolygon!!,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun FinalPolygonPreview(
    polygon: Polygon,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(Color.White)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (polygon.points.isEmpty()) return@Canvas

            val minX = polygon.points.minOf { it.x }
            val maxX = polygon.points.maxOf { it.x }
            val minY = polygon.points.minOf { it.y }
            val maxY = polygon.points.maxOf { it.y }

            val polygonWidth = (maxX - minX).coerceAtLeast(1.0)
            val polygonHeight = (maxY - minY).coerceAtLeast(1.0)

            val padding = 32f

            val availableWidth = size.width - 2 * padding
            val availableHeight = size.height - 2 * padding

            val scale = minOf(
                availableWidth / polygonWidth.toFloat(),
                availableHeight / polygonHeight.toFloat()
            )

            val drawnWidth = polygonWidth.toFloat() * scale
            val drawnHeight = polygonHeight.toFloat() * scale

            val offsetX = (size.width - drawnWidth) / 2f
            val offsetY = (size.height - drawnHeight) / 2f

            fun toCanvas(pointX: Double, pointY: Double): Offset {
                val localX = (pointX - minX).toFloat() * scale
                val localY = (pointY - minY).toFloat() * scale

                return Offset(
                    x = offsetX + localX,
                    y = offsetY + localY
                )
            }

            val screenPoints = polygon.points.map { toCanvas(it.x, it.y) }

            for (i in screenPoints.indices) {
                val p1 = screenPoints[i]
                val p2 = screenPoints[(i + 1) % screenPoints.size]

                drawLine(
                    color = Color.Blue,
                    start = p1,
                    end = p2,
                    strokeWidth = 4f
                )
            }

            screenPoints.forEachIndexed { index, point ->
                drawCircle(
                    color = if (index == 0) Color.Magenta else Color.Red,
                    radius = 8f,
                    center = point
                )
            }
        }
    }
}