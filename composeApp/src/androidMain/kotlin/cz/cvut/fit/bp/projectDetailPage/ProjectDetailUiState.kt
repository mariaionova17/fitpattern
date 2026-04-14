package cz.cvut.fit.bp.projectDetailPage

import domain.project.Project
import domain.solver.NestingResult

data class ProjectDetailUiState(
    val isLoading: Boolean = false,
    val project: Project? = null,
    val generatedLayouts: List<GeneratedLayoutCardUi> = emptyList(),
    val errorMessage: String? = null
)