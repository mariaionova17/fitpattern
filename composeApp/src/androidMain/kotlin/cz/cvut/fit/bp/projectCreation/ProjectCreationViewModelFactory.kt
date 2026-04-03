package cz.cvut.fit.bp.projectCreation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import domain.repository.ProjectRepository

class ProjectCreationViewModelFactory(
    private val projectRepository: ProjectRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectCreationViewModel::class.java)) {
            return ProjectCreationViewModel(projectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}