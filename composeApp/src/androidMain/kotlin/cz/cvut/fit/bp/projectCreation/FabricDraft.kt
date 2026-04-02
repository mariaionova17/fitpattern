package cz.cvut.fit.bp.projectCreation

import cz.cvut.fit.bp.imageRecognition.markerDetection.Marker
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import domain.geometry.Point

data class FabricDraft(
    val inputMode: FabricInputMode? = null,
    val lengthMm: String = "",
    val widthMm: String = "",
    val warpAlongWidth: Boolean = false,
    val imageUri: String? = null,
    val resourceImageName: String? = null,
    val detectedContour: List<CvPoint>? = null,
    val editedContour: List<CvPoint>? = null,
    val detectedA4Marker: Marker? = null,
    val editedA4Marker: Marker? = null,
    val scaledContourMm: List<Point>? = null,
    val warpAngleDegreesFromVertical: Double = 0.0,
    val isDetectingContour: Boolean = false,
    val errorMessage: String? = null
)

enum class FabricInputMode {
    IMAGE,
    MANUAL
}