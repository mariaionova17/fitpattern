package cz.cvut.fit.bp.projectDetailPage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fit.bp.projectCreation.ProjectCreationUiState
import domain.repository.ProjectRepository
import kotlinx.coroutines.launch

class ProjectDetailViewModel(private val projectRepository: ProjectRepository) : ViewModel() {
    var uiState by mutableStateOf(ProjectDetailUiState())
        private set

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                val project = projectRepository.getProjectById(projectId)
                uiState = uiState.copy(
                    isLoading = false,
                    project = project
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
