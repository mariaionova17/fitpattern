package domain.geometry

fun Shape.boundingBox(): BoundingBox{
    return contour.boundingBox()
}