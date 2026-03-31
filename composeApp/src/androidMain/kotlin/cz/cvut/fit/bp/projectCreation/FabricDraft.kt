package cz.cvut.fit.bp.projectCreation

import domain.geometry.Point

data class FabricDraft(
    val inputMode: FabricInputMode? = null,
    val lengthMm: String = "",
    val widthMm: String = "",
    val imageUri: String? = null,
    val detectedContour: List<Point>? = null,
    val editedContour: List<Point>? = null,
    val isDetectingContour: Boolean = false,
    val errorMessage: String? = null
)

enum class FabricInputMode {
    IMAGE,
    MANUAL
}