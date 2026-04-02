package cz.cvut.fit.bp.projectCreation

import cz.cvut.fit.bp.imageRecognition.markerDetection.alignPolygonWarpToVertical
import cz.cvut.fit.bp.imageRecognition.markerDetection.editablePolygonToMmPolygon
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import domain.fabric.Fabric
import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.pattern.PlacementRules
import domain.pattern.RotationRules
import domain.project.Project
import java.util.UUID

fun UiRotationOption.toRotationRules(): RotationRules {
    val result = when (this){
        UiRotationOption.NONE -> listOf(0.0)
        UiRotationOption.BY180 -> listOf(0.0, 180.0)
        UiRotationOption.BY90 -> listOf(0.0, 90.0, 180.0, 270.0)
        UiRotationOption.ANY -> listOf(0.0, 90.0, 180.0, 270.0)   //TODO solve this
    }
    return RotationRules(result)
}

fun ProjectCreationUiState.toPlacementRules(): PlacementRules{
    return PlacementRules(rotationOption.toRotationRules(), allowMirror)
}

fun FabricDraft.toManualFabric(): Fabric{
    var width = widthMm.toDoubleOrNull() ?: error("Invalid width")
    var length = lengthMm.toDoubleOrNull() ?: error("Invalid length")

    require(width > 0) { "Width must be greater than 0" }
    require(length > 0) { "Length must be greater than 0" }

    if (warpAlongWidth) {  // so warp is always along y
        val i = length
        length = width
        width = i
    }

    val polygon = Polygon(listOf(
        Point(0.0, 0.0),
        Point(width, 0.0),
        Point(width, length),
        Point(0.0, length)
    ))

    return Fabric(UUID.randomUUID().toString(), Shape(polygon))
}

fun FabricDraft.toImageFabric(): Fabric{
    val contour = editedContour ?: detectedContour ?: error("No contour available for image fabric")
    val marker = editedA4Marker ?: detectedA4Marker ?: error("No marker available for image fabric")
    val normalizedContour = editablePolygonToMmPolygon(contour, marker)
    val contourWithWarpVertically = alignPolygonWarpToVertical(normalizedContour, warpAngleDegreesFromVertical)
    return Fabric(UUID.randomUUID().toString(), Shape(contourWithWarpVertically))
}

fun ProjectCreationUiState.toProject(): Project{
    val seamAllowanceValue = when {
        seamAllowance.isBlank() -> 0.0
        seamAllowance.toDoubleOrNull() != null -> seamAllowance.toDouble()
        else -> error("Seam allowance must be a valid number")
    }
    return Project(projectName,
        addedFabrics,
        toPlacementRules(),
        seamAllowanceValue)
}

fun List<CvPoint>.toDomainPoints(): List<Point> {
    return map { cvPoint ->
        Point(cvPoint.x, cvPoint.y)
    }
}
