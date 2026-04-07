package cz.cvut.fit.bp

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import domain.geometry.Point
import domain.geometry.Polygon
import domain.layout.Placement
import domain.geometry.Shape
import domain.geometry.apply
import domain.geometry.boundingBox
import domain.pattern.PatternPiece

@Composable
fun LayoutPreview(
    fabric: Shape,
    piecesById: Map<String, PatternPiece>,
    piecesByIdSeamAllowance: Map<String, PatternPiece>,
    placements: List<Placement>,
    modifier: Modifier = Modifier,
    seamAllowance: Double = 10.0
) {
    Canvas(modifier = modifier) {
        val fabricBox = fabric.contour.boundingBox()

        val scaleX = size.width / (fabricBox.maxX - fabricBox.minX).toFloat()
        val scaleY = size.height / (fabricBox.maxY - fabricBox.minY).toFloat()
        val scale = minOf(scaleX, scaleY)

        fun toCanvas(point: Point): Offset {
            return Offset(
                x = size.width - ((point.x - fabricBox.minX) * scale).toFloat(),
                y = size.height - ((point.y - fabricBox.minY) * scale).toFloat()
            )
        }

        fun polygonPath(polygon: Polygon): Path {
            val path = Path()
            val pts = polygon.points
            if (pts.isEmpty()) return path

            val first = toCanvas(pts.first())
            path.moveTo(first.x, first.y)

            for (i in 1 until pts.size) {
                val p = toCanvas(pts[i])
                path.lineTo(p.x, p.y)
            }

            path.close()
            return path
        }

        drawPath(
            path = polygonPath(fabric.contour),
            color = Color.LightGray,
            style = Stroke(width = 3f)
        )

        placements.forEach { placement ->
            val originalPiece = piecesById[placement.pieceId] ?: return@forEach
            val seamPiece = piecesByIdSeamAllowance[placement.pieceId] ?: return@forEach

            val placedOriginal = originalPiece.shape.apply(placement.transformation)
            val placedSeam = seamPiece.shape.apply(placement.transformation)

            drawPath(
                path = polygonPath(placedSeam.contour),
                color = Color.Red,
                style = Stroke(width = 1.5f)
            )

            drawPath(
                path = polygonPath(placedOriginal.contour),
                color = Color.Blue,
                style = Stroke(width = 2f)
            )
        }
    }
}