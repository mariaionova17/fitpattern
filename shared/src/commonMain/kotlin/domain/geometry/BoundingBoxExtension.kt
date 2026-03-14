package domain.geometry

import kotlin.math.abs

fun BoundingBox.width(): Double = maxX - minX
fun BoundingBox.height(): Double = maxY - minY

fun BoundingBox.movedBy(offset: Point): BoundingBox =
    BoundingBox(
        minX = minX + offset.x,
        minY = minY + offset.y,
        maxX = maxX + offset.x,
        maxY = maxY + offset.y
    )

fun BoundingBox.contains(other: BoundingBox): Boolean =
    other.minX >= minX && other.maxX <= maxX && other.minY >= minY && other.maxY <= maxY

fun BoundingBox.intersects(other: BoundingBox): Boolean =
    !(other.minX > maxX || other.maxX < minX || other.minY > maxY || other.maxY < minY)

fun BoundingBox.isOnTheBorderOf(other: BoundingBox, eps: Double = 1e-6): Boolean =
    abs(other.minX - minX) <= eps || abs(other.maxX - maxX) <= eps ||
            abs(other.minY - minY) <= eps || abs(other.maxY - maxY) <= eps
