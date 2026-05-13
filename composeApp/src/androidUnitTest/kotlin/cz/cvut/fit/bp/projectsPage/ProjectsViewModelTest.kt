package cz.cvut.fit.bp.projectsPage

import cz.cvut.fit.bp.FakeProjectRepository
import cz.cvut.fit.bp.testutil.MainDispatcherRule
import domain.pattern.PlacementRules
import domain.project.Project
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ProjectsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadProjects stores loaded projects and clears loading`() = runTest {
        val repository = FakeProjectRepository()
        val projects = listOf(
            Project(
                id = "project-1",
                name = "Project One",
                fabricPieces = emptyList(),
                rules = PlacementRules(),
                seamAllowance = 1.0
            ),
            Project(
                id = "project-2",
                name = "Project Two",
                fabricPieces = emptyList(),
                rules = PlacementRules(),
                seamAllowance = 2.0
            )
        )
        repository.allProjectsResult = projects

        val viewModel = ProjectsViewModel(repository)

        viewModel.loadProjects()
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.isLoading)
        assertEquals(projects, viewModel.uiState.projects)
        assertNull(viewModel.uiState.errorMessage)
    }

    @Test
    fun `loadProjects stores error message when repository throws`() = runTest {
        val repository = FakeProjectRepository()
        repository.getAllProjectsException = IllegalStateException("Projects failed")

        val viewModel = ProjectsViewModel(repository)

        viewModel.loadProjects()
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.isLoading)
        assertEquals(emptyList(), viewModel.uiState.projects)
        assertEquals("Projects failed", viewModel.uiState.errorMessage)
    }
}
