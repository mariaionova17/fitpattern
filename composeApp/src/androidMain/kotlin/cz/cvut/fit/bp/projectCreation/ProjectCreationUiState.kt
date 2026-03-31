package cz.cvut.fit.bp.projectCreation

import domain.fabric.Fabric

data class ProjectCreationUiState(
    val projectName: String = "",
    val seamAllowance: String = "",
    val rotationOption: UiRotationOption = UiRotationOption.NONE,
    val allowMirror: Boolean = false,
    val currentFabric: FabricDraft = FabricDraft(),
    val addedFabrics: List<Fabric> = emptyList(),
    val errorMessage: String? = null,
    val isManualDialogOpen: Boolean = false
)

enum class UiRotationOption{
    NONE,
    BY180,
    BY90,
    ANY
}