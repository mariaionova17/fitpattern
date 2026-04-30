package cz.cvut.fit.bp

import android.R.attr.textSize
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import android.graphics.Paint
@Composable
fun LayoutPreview(
    fabric: Shape,
    piecesById: Map<String, PatternPiece>,
    piecesByIdSeamAllowance: Map<String, PatternPiece>,
    placements: List<Placement>,
    modifier: Modifier = Modifier,
    fabricIndex: Int,
    seamAllowance: Double = 10.0
) {
    Canvas(modifier = modifier) {
        val fabricBox = fabric.contour.boundingBox()

        val fabricWidth = (fabricBox.maxX - fabricBox.minX).toFloat()
        val fabricHeight = (fabricBox.maxY - fabricBox.minY).toFloat()

        val scaleX = size.width / fabricWidth
        val scaleY = size.height / fabricHeight
        val scale = minOf(scaleX, scaleY)

        val drawnWidth = fabricWidth * scale
        val drawnHeight = fabricHeight * scale

        val offsetX = (size.width - drawnWidth) / 2f
        val offsetY = (size.height - drawnHeight) / 2f

        fun toCanvas(point: Point): Offset {
            return Offset(
                x = ((point.x - fabricBox.minX).toFloat() * scale) + offsetX,
                y = size.height - (((point.y - fabricBox.minY).toFloat() * scale) + offsetY)
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

        val placementsForThisFabric = placements.filter { it.fabricPieceIndex == fabricIndex }
        placementsForThisFabric.forEachIndexed { index, placement ->
            val originalPiece = piecesById[placement.pieceId] ?: return@forEachIndexed
            val seamPiece = piecesByIdSeamAllowance[placement.pieceId] ?: return@forEachIndexed

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

            val box = placedOriginal.contour.boundingBox()
            val center = toCanvas(
                Point(
                    x = (box.minX + box.maxX) / 2.0,
                    y = (box.minY + box.maxY) / 2.0
                )
            )

            drawContext.canvas.nativeCanvas.drawText(
                "${index + 1}",
                center.x,
                center.y,
                Paint().apply {
                    color = Color.Black.toArgb()
                    textSize = 32f
                    textAlign = Paint.Align.CENTER
                    isFakeBoldText = true
                }
            )
        }
    }
}