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

fun BoundingBox.contains(other: BoundingBox, eps: Double = 1e-2): Boolean =
    other.minX >= minX - eps && other.maxX <= maxX + eps && other.minY >= minY - eps && other.maxY <= maxY + eps

fun BoundingBox.intersects(other: BoundingBox, eps: Double = 1e-2): Boolean =
    other.minX <= maxX + eps && other.maxX >= minX - eps && other.minY <= maxY + eps && other.maxY >= minY - eps

fun BoundingBox.isOnTheBorderOf(other: BoundingBox, eps: Double = 1e-6): Boolean =
    abs(other.minX - minX) <= eps || abs(other.maxX - maxX) <= eps ||
            abs(other.minY - minY) <= eps || abs(other.maxY - maxY) <= eps
