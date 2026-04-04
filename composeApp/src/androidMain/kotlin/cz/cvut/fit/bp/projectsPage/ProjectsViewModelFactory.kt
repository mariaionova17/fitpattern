package cz.cvut.fit.bp.projectsPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import domain.repository.ProjectRepository

class ProjectsViewModelFactory(
    private val projectRepository: ProjectRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectsViewModel::class.java)) {
            return ProjectsViewModel(projectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}