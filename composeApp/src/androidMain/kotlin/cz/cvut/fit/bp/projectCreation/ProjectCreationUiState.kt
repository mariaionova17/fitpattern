package cz.cvut.fit.bp.projectCreation

import domain.fabric.FabricPiece

data class ProjectCreationUiState(
    val projectName: String = "",
    val seamAllowance: String = "",
    val rotationOption: UiRotationOption = UiRotationOption.NONE,
    val allowMirror: Boolean = false,
    val currentFabric: FabricDraft = FabricDraft(),
    val addedFabricPieces: List<FabricPiece> = emptyList(),
    val errorMessage: String? = null,
    val isManualDialogOpen: Boolean = false,
    val currentImageStep: FabricImageStep = FabricImageStep.NONE
)

enum class UiRotationOption{
    NONE,
    BY180,
    BY90,
    ANY
}

enum class FabricImageStep {
    NONE,
    EDIT_CONTOUR,
    EDIT_A4,
    EDIT_WARP
}