package cz.cvut.fit.bp.projectCreation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fit.bp.imageRecognition.markerDetection.Marker
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import domain.project.Project
import domain.repository.ProjectRepository
import kotlinx.coroutines.launch
import java.util.UUID

class ProjectCreationViewModel(private val projectRepository: ProjectRepository) : ViewModel() {
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
                addedFabricPieces = uiState.addedFabricPieces + fabric,
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
                addedFabricPieces = uiState.addedFabricPieces + fabric,
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
            if (uiState.addedFabricPieces.isEmpty()) error("Add at least one fabric")

            val projectId = uiState.id.ifBlank {
                val newId = UUID.randomUUID().toString()
                uiState = uiState.copy(id = newId)
                newId
            }
            uiState.toProject(projectId)
        } catch (e: Exception) {
            uiState = uiState.copy(errorMessage = e.message)
            null
        }
    }

    fun saveProject(onSaved: (String) -> Unit) {
        val project = buildProject() ?: return

        viewModelScope.launch {
            try {
                projectRepository.saveProject(project)
                uiState = uiState.copy(errorMessage = null)
                onSaved(project.id)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message)
            }
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
                resourceImageName = null,
                detectedContour = null,
                editedContour = null,
                errorMessage = null
            ),
            currentImageStep = FabricImageStep.EDIT_CONTOUR,
            errorMessage = null
        )
    }

    fun confirmImageFabric() {
        try {
            val fabric = uiState.currentFabric.toImageFabric()

            uiState = uiState.copy(
                addedFabricPieces = uiState.addedFabricPieces + fabric,
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
                errorMessage = null,
            ),
            currentImageStep = FabricImageStep.EDIT_CONTOUR,
            errorMessage = null
        )
    }

    fun clearImageDraft() {
        uiState = uiState.copy(
            currentFabric = FabricDraft(),
            errorMessage = null
        )
    }

    fun confirmEditedContour(contour: List<CvPoint>) {
        uiState = uiState.copy(
            currentFabric = uiState.currentFabric.copy(
                editedContour = contour,
                errorMessage = null
            ),
            currentImageStep = FabricImageStep.EDIT_A4,
            errorMessage = null
        )
    }

    fun confirmEditedA4(marker: Marker) {
        uiState = uiState.copy(
            currentFabric = uiState.currentFabric.copy(
                editedA4Marker = marker,
                errorMessage = null
            ),
            currentImageStep = FabricImageStep.EDIT_WARP,
            errorMessage = null
        )
    }

    fun confirmWarpTilt(degrees: Double) {
        uiState = uiState.copy(
            currentFabric = uiState.currentFabric.copy(
                warpAngleDegreesFromVertical = degrees,
                errorMessage = null
            ),
            errorMessage = null
        )
    }
}

