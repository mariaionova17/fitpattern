package domain.geometry

fun Shape.boundingBox(): BoundingBox{
    return contour.boundingBox()
}

//function returns shape's measurements if its a rectangle made from measurements, null if not
fun Shape.rectangleSize(): Pair<Double, Double>? {
    if (contour.points.size != 4 || holes.isNotEmpty())
        return null

    if (contour.points[1].x == contour.points[2].x &&
        contour.points[0].x == contour.points[3].x &&
        contour.points[0].y == contour.points[1].y &&
        contour.points[2].y == contour.points[3].y )
        return Pair(contour.points[2].x, contour.points[2].y)

    return null
}