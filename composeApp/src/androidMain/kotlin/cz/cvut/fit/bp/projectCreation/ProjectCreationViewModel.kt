package cz.cvut.fit.bp.projectCreation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import domain.project.Project
import kotlin.plus

class ProjectCreationViewModel : ViewModel() {
    var uiState by mutableStateOf(ProjectCreationUiState())
        private set

    fun onProjectNameChanged(value: String) {
        uiState = uiState.copy(projectName = value)
    }

    fun onSeamAllowanceChanged(value: String) {
        uiState = uiState.copy(seamAllowance = value)
    }

    fun onRotationChanged(value: UiRotationOption) {
        uiState = uiState.copy(rotationOption = value)
    }

    fun onAllowMirrorChanged(value: Boolean) {
        uiState = uiState.copy(allowMirror = value)
    }

    fun onFabricModeChanged(value: FabricInputMode) {
        uiState = uiState.copy(
            currentFabric = uiState.currentFabric.copy(inputMode = value)
        )
    }

    fun onFabricWidthChanged(value: String) {
        uiState = uiState.copy(
            currentFabric = uiState.currentFabric.copy(widthMm = value)
        )
    }

    fun onFabricLengthChanged(value: String) {
        uiState = uiState.copy(
            currentFabric = uiState.currentFabric.copy(lengthMm = value)
        )
    }

    fun onWarpChanged(value: Boolean) {
        uiState = uiState.copy(
            currentFabric = uiState.currentFabric.copy(warpAlongWidth = value)
        )
    }

    fun openManualDialog() {
        uiState = uiState.copy(
            isManualDialogOpen = true,
            currentFabric = uiState.currentFabric.copy(inputMode = FabricInputMode.MANUAL)
        )
    }

    fun closeManualDialog() {
        uiState = uiState.copy(isManualDialogOpen = false)
    }

    fun confirmManualDialog() {
        try {
            val fabric = uiState.currentFabric.toManualFabric()

            uiState = uiState.copy(
                addedFabrics = uiState.addedFabrics + fabric,
                currentFabric = FabricDraft(),
                isManualDialogOpen = false,
                errorMessage = null
            )
        } catch (e: Exception) {
            uiState = uiState.copy(
                errorMessage = e.message
            )
        }
    }

    fun addCurrentFabric() {
        try {
            val fabric = when (uiState.currentFabric.inputMode) {
                FabricInputMode.MANUAL -> uiState.currentFabric.toManualFabric()
                FabricInputMode.IMAGE -> uiState.currentFabric.toImageFabric()
                null -> error("Please select fabric input mode")
            }

            uiState = uiState.copy(
                addedFabrics = uiState.addedFabrics + fabric,
                currentFabric = FabricDraft(),
                errorMessage = null
            )
        } catch (e: Exception) {
            uiState = uiState.copy(
                errorMessage = e.message
            )
        }
    }

    fun buildProject(): Project? {
        return try {
            if (uiState.projectName.isBlank()) error("Project name is required")
            if (uiState.addedFabrics.isEmpty()) error("Add at least one fabric")

            uiState.toProject()
        } catch (e: Exception) {
            uiState = uiState.copy(errorMessage = e.message)
            null
        }
    }

    fun startImageMode() {
        uiState = uiState.copy(
            currentFabric = FabricDraft(inputMode = FabricInputMode.IMAGE),
            errorMessage = null
        )
    }

    fun setImageUri(uri: String) {
        uiState = uiState.copy(
            currentFabric = uiState.currentFabric.copy(
                inputMode = FabricInputMode.IMAGE,
                imageUri = uri,
                detectedContour = null,
                editedContour = null,
                errorMessage = null
            )
        )
    }

    fun runContourDetection() {
        val uri = uiState.currentFabric.imageUri ?: return

        uiState = uiState.copy(
            currentFabric = uiState.currentFabric.copy(isDetectingContour = true),
            errorMessage = null
        )

        try {
            // TODO: replace with real recognition result
            val fakeContour = listOf(
                domain.geometry.Point(0.0, 0.0),
                domain.geometry.Point(100.0, 0.0),
                domain.geometry.Point(100.0, 80.0),
                domain.geometry.Point(0.0, 80.0)
            )

            uiState = uiState.copy(
                currentFabric = uiState.currentFabric.copy(
                    isDetectingContour = false,
                    detectedContour = fakeContour
                )
            )
        } catch (e: Exception) {
            uiState = uiState.copy(
                currentFabric = uiState.currentFabric.copy(isDetectingContour = false),
                errorMessage = e.message
            )
        }
    }

    fun confirmImageFabric() {
        try {
            val fabric = uiState.currentFabric.toImageFabric()

            uiState = uiState.copy(
                addedFabrics = uiState.addedFabrics + fabric,
                currentFabric = FabricDraft(),
                errorMessage = null
            )
        } catch (e: Exception) {
            uiState = uiState.copy(
                errorMessage = e.message
            )
        }
    }



    fun setResourceImage(name: String) {
        uiState = uiState.copy(
            currentFabric = uiState.currentFabric.copy(
                inputMode = FabricInputMode.IMAGE,
                imageUri = null,
                resourceImageName = name,
                detectedContour = null,
                editedContour = null,
                errorMessage = null
            )
        )
    }

    fun clearImageDraft() {
        uiState = uiState.copy(
            currentFabric = FabricDraft(),
            errorMessage = null
        )
    }

    fun addImageFabricFromCvContour(contour: List<CvPoint>) {
        try {
            val domainContour = contour.toDomainPoints()

            val fabric = FabricDraft(
                inputMode = FabricInputMode.IMAGE,
                editedContour = domainContour
            ).toImageFabric()

            uiState = uiState.copy(
                addedFabrics = uiState.addedFabrics + fabric,
                currentFabric = FabricDraft(),
                errorMessage = null
            )
        } catch (e: Exception) {
            uiState = uiState.copy(
                errorMessage = e.message
            )
        }
    }
}

