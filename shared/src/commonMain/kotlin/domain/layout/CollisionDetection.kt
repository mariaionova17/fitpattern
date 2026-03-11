package domain.layout

import domain.geometry.Polygon
import domain.geometry.Shape

interface GeometryKernel {
    fun intersects(a: Polygon, b: Polygon): Boolean
    fun intersects(a: Shape, b: Shape): Boolean
    fun contains(container: Polygon, subject: Polygon): Boolean
    fun contains(container: Shape, subject: Shape): Boolean
}