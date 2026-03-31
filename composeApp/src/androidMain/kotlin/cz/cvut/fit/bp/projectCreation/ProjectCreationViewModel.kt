package cz.cvut.fit.bp.projectCreation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import domain.project.Project

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
}