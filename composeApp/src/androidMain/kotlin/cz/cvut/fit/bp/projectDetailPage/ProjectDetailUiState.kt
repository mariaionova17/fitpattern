package cz.cvut.fit.bp.projectDetailPage

import cz.cvut.fit.bp.projectCreation.UiRotationOption
import domain.project.Project
import domain.solver.NestingResult

data class ProjectDetailUiState(
    val isLoading: Boolean = false,
    val project: Project? = null,
    val generatedLayouts: List<GeneratedLayoutCardUi> = emptyList(),
    val errorMessage: String? = null,
    val isEditProjectDialogOpen: Boolean = false,
    val isDeleteLayoutsAfterEditDialogOpen: Boolean = false,
    val editDraft: ProjectEditDraft = ProjectEditDraft()
)

data class ProjectEditDraft(
    val name: String = "",
    val seamAllowance: String = "",
    val rotationOption: UiRotationOption = UiRotationOption.NONE,
    val allowMirror: Boolean = false
)