package cz.cvut.fit.bp.imageRecognition.markerDetection
import cz.cvut.fit.bp.imageRecognition.CvPoint
import domain.geometry.Point
import domain.geometry.Polygon
import org.opencv.core.Mat

fun editablePolygonToMmPolygon(
    editablePolygon: List<CvPoint>,
    marker: Marker
): Polygon {
    val homography = computeA4Homography(marker)
    val rectifiedPolygon = transformPolygon(editablePolygon, homography)

    // A4 rectified to 2100x2970 px => 10 px = 1 mm
    val mmPoints = rectifiedPolygon.map {
        Point(
            x = it.x / 10.0,
            y = it.y / 10.0
        )
    }

    return normalizePolygonToBottomLeft(Polygon(mmPoints))
}
fun normalizePolygonToBottomLeft(polygon: Polygon): Polygon {
    if (polygon.points.isEmpty()) return polygon

    val minX = polygon.points.minOf { it.x }
    val maxY = polygon.points.maxOf { it.y }

    return Polygon(
        points = polygon.points.map {
            Point(
                x = it.x - minX,
                y = maxY - it.y
            )
        }
    )
}