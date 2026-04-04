package cz.cvut.fit.bp.projectDetailPage

import domain.project.Project

data class ProjectDetailUiState(
    val isLoading: Boolean = false,
    val project: Project? = null,
    val errorMessage: String? = null
)