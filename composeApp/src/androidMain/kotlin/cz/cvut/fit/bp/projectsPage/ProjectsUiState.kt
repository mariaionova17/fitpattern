package cz.cvut.fit.bp.projectsPage

import domain.project.Project

data class ProjectsUiState(
    val isLoading: Boolean = false,
    val projects: List<Project> = emptyList(),
    val errorMessage: String? = null
)