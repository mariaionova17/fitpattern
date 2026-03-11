package domain.geometry

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
