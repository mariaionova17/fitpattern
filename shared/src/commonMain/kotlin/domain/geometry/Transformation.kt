package domain.geometry
import kotlin.math.*


data class Transformation(
    val move: Point = Point(0.0, 0.0),
    val rotationDegrees: Double = 0.0,
    val mirrorY: Boolean = false
)
fun Polygon.rotate(angle: Double): Polygon{
    val angleRadians = angle * PI / 180.0
    val cos = cos(angleRadians)
    val sin = sin(angleRadians)

    val pointsRotated = points.map { point ->
        Point (
            x = point.x * cos - point.y * sin,
            y = point.x * sin + point.y * cos
        )
    }
    return Polygon(pointsRotated)
}

fun Polygon.move(offset: Point): Polygon{
    val pointsMoved = points.map { point ->
        Point (
            x = point.x + offset.x,
            y = point.y + offset.y
        )
    }
    return Polygon(pointsMoved)
}

fun Polygon.mirrorX(): Polygon{
    val pointsMirrored = points.map { point ->
        Point (
            x = point.x,
            y = - point.y
        )
    }
    return Polygon(pointsMirrored)
}

fun Polygon.mirrorY(): Polygon{
    val pointsMirrored = points.map { point ->
        Point (
            x = - point.x,
            y = point.y
        )
    }
    return Polygon(pointsMirrored)
}

fun Polygon.mirrorYAroundCenter(): Polygon {
    val center = center()
    return move(Point(-center.x, -center.y))
        .mirrorY()
        .move(center)
}

fun Polygon.rotateAroundCenter(angle: Double): Polygon{
    val center = center()
    return this
        .move(Point(-center.x, -center.y))
        .rotate(angle)
        .move(center)
}

fun Polygon.apply(transformation: Transformation): Polygon{
    var polygon = this
    if (transformation.mirrorY)
        polygon = polygon.mirrorYAroundCenter()
    polygon = polygon.rotateAroundCenter(transformation.rotationDegrees)
    polygon = polygon.move(transformation.move)
    return polygon
}

fun Shape.apply(transformation: Transformation): Shape =
    Shape(
        contour = contour.apply(transformation),
        holes = holes.map {it.apply(transformation)}
    )
