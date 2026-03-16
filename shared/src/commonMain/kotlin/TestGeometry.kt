import domain.fabric.Fabric
import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.layout.GeometryKernel
import domain.layout.SimpleGeometryKernel

fun testGeometry(){
    val geometry: GeometryKernel = SimpleGeometryKernel()
    val square = Polygon(listOf(
        Point(0.0, 0.0),
        Point(100.0, 0.0),
        Point(100.0, 100.0),
        Point(0.0, 100.0)))
    val square2 = Polygon(listOf(
        Point(100.0, 0.0),
        Point(200.0, 0.0),
        Point(200.0, 100.0),
        Point(100.0, 100.0)))
    val square3 = Polygon(listOf(
        Point(100.0, 100.0),
        Point(200.0, 100.0),
        Point(200.0, 200.0),
        Point(100.0, 200.0)))
    val square0 = Polygon(listOf(
        Point(0.0, 0.0),
        Point(100.0, 0.0),
        Point(100.0, 100.0),
        Point(0.0, 100.0)))

    val square4 = Polygon(listOf(
        Point(0.0, 0.0),
        Point(10.0, 0.0),
        Point(10.0, 100.0),
        Point(0.0, 100.0)))

    val square5 = Polygon(listOf(
        Point(50.0, 0.0),
        Point(150.0, 0.0),
        Point(150.0, 100.0),
        Point(50.0, 100.0)))

    val tri = Polygon(listOf(
        Point(0.0, 0.0),
        Point(100.0, 0.0),
        Point(50.0, 60.0)))

    val tri2 = Polygon(listOf(
        Point(100.0, 0.0),
        Point(150.0, 60.0),
        Point(50.0, 60.0)))

    val tri3 = Polygon(listOf(
        Point(50.0, 60.0),
        Point(150.0, 60.0),
        Point(100.0, 0.0)
    ))
    val tri4 = Polygon(listOf(
        Point(50.0, 0.0),
        Point(150.0, 0.0),
        Point(100.0, 60.0)
    ))

    if (geometry.intersects(square, square2))
        println("Yes, need No")
    else
        println("+No, need No")

    if (geometry.intersects(square2, square3))
        println("Yes, need No")
    else
        println("+No, need No")

    if (geometry.intersects(square, square3))
        println("Yes, need No")
    else
        println("+No, need No")

    if (geometry.intersects(square, square0))
        println("+Yes, need Yes")
    else
        println("No, need Yes")

    if (geometry.intersects(square, square4))
        println("+Yes, need Yes")
    else
        println("No, need Yes")

    if (geometry.intersects(square, square5))
        println("+Yes, need Yes")
    else
        println("No, need Yes")

    if (geometry.intersects(tri, tri2))
        println("Yes, need No")
    else
        println("+No, need No")

    if (geometry.intersects(tri, tri3))
        println("Yes, need No")
    else
        println("+No, need No")

    if (geometry.intersects(tri, tri4))
        println("+Yes, need Yes")
    else
        println("No, need Yes")
}