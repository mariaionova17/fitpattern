package cz.cvut.fit.bp.projectDetailPage

import cz.cvut.fit.bp.projectCreation.FabricDraft
import cz.cvut.fit.bp.projectCreation.FabricImageStep
import cz.cvut.fit.bp.projectCreation.UiRotationOption
import domain.fabric.FabricPiece
import domain.project.Project

data class ProjectDetailUiState(
    val isLoading: Boolean = false,
    val project: Project? = null,
    val generatedLayouts: List<GeneratedLayoutCardUi> = emptyList(),
    val errorMessage: String? = null,

    val isEditProjectDialogOpen: Boolean = false,
    val isDeleteLayoutsAfterEditDialogOpen: Boolean = false,
    val editDraft: ProjectEditDraft = ProjectEditDraft(),

    val isFabricOptionsDialogOpen: Boolean = false,
    val isManualFabricDialogOpen: Boolean = false,
    val isDeleteLayoutsAfterFabricChangeDialogOpen: Boolean = false,

    val fabricDraft: FabricDraft = FabricDraft(),
    val currentImageStep: FabricImageStep = FabricImageStep.NONE,

    val pendingFabricMutation: PendingFabricMutation? = null,
    val selectedFabricIndexForAction: Int? = null
)

data class ProjectEditDraft(
    val name: String = "",
    val seamAllowance: String = "",
    val rotationOption: UiRotationOption = UiRotationOption.NONE,
    val allowMirror: Boolean = false
)

sealed interface PendingFabricMutation {
    data class Add(val fabric: FabricPiece) : PendingFabricMutation
    data class Delete(val index: Int) : PendingFabricMutation
}