package cz.cvut.fit.bp.projectsPage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.repository.ProjectRepository
import kotlinx.coroutines.launch

class ProjectsViewModel(private val projectRepository: ProjectRepository) : ViewModel() {
    var uiState by mutableStateOf(ProjectsUiState())
        private set

    fun loadProjects() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                val projects = projectRepository.getAllProjects()
                uiState = uiState.copy(
                    isLoading = false,
                    projects = projects
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }
}
