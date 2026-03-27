package cz.cvut.fit.bp.imageRecognition

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import domain.geometry.Polygon
import domain.geometry.Point

@Composable
fun ResultPolygonPreview(
    polygon: Polygon,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        if (polygon.points.size < 2) return@Canvas

        val padding = 40f

        val minX = polygon.points.minOf { it.x }
        val maxX = polygon.points.maxOf { it.x }
        val minY = polygon.points.minOf { it.y }
        val maxY = polygon.points.maxOf { it.y }

        val polygonWidth = (maxX - minX).coerceAtLeast(1.0)
        val polygonHeight = (maxY - minY).coerceAtLeast(1.0)

        val availableWidth = size.width - 2 * padding
        val availableHeight = size.height - 2 * padding

        val scale = minOf(
            availableWidth / polygonWidth.toFloat(),
            availableHeight / polygonHeight.toFloat()
        )

        fun mapPoint(point: Point): Offset {
            val x = padding + ((point.x - minX) * scale).toFloat()

            val y = size.height - padding - ((point.y - minY) * scale).toFloat()

            return Offset(x, y)
        }

        val screenPoints = polygon.points.map { mapPoint(it) }

        // polygon edges
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

        // vertices
        screenPoints.forEach { p ->
            drawCircle(
                color = Color.Red,
                radius = 8f,
                center = p
            )
        }
    }
}