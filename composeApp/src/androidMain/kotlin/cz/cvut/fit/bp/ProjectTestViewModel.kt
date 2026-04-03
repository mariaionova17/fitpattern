package cz.cvut.fit.bp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.pattern.PlacementRules
import domain.pattern.RotationRules
import domain.project.Project
import domain.repository.ProjectRepository
import kotlinx.coroutines.launch

class ProjectTestViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    fun testDatabase() {
        viewModelScope.launch {
            val project = Project(
                id = "project_1",
                name = "Test project",
                fabricPieces = emptyList(),
                rules = PlacementRules(
                    rotation = RotationRules(listOf(0.0, 90.0)),
                    allowMirror = true
                ),
                seamAllowance = 1.5
            )

            projectRepository.saveProject(project)

            val loaded = projectRepository.getProjectById("project_1")
            println("Loaded project: $loaded")
        }
    }
}