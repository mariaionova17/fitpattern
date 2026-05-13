package cz.cvut.fit.bp.projectDetailPage

import cz.cvut.fit.bp.FakeNestingResultRepository
import cz.cvut.fit.bp.FakePatternRepository
import cz.cvut.fit.bp.FakeProjectRepository
import cz.cvut.fit.bp.testutil.MainDispatcherRule
import domain.layout.LayoutScore
import domain.layout.LayoutState
import domain.pattern.PlacementRules
import domain.project.Project
import domain.solver.NestingResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProjectDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadProject stores project and generated layouts`() = runTest {
        val projectRepository = FakeProjectRepository()
        val patternRepository = FakePatternRepository()
        val nestingResultRepository = FakeNestingResultRepository()

        val project = Project(
            id = "project-1",
            name = "My Project",
            fabricPieces = emptyList(),
            rules = PlacementRules(),
            seamAllowance = 1.5
        )
        projectRepository.projectByIdResult = project
        nestingResultRepository.resultsByProjectId["project-1"] = listOf(
            NestingResult(
                id = "result-1",
                bestLayout = LayoutState(
                    placed = emptyList(),
                    unplaced = emptyList(),
                    score = LayoutScore(0.0, 0.0)
                ),
                triedLayouts = 0,
                timeMs = 0
            )
        )
        nestingResultRepository.nameAndVariantByResultId["result-1"] = "Basic T-Shirt" to "variant-m"
        patternRepository.sizeByVariantId["variant-m"] = "M"

        val viewModel = ProjectDetailViewModel(
            projectRepository = projectRepository,
            patternRepository = patternRepository,
            nestingResultRepository = nestingResultRepository
        )

        viewModel.loadProject("project-1")
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.isLoading)
        assertEquals(project, viewModel.uiState.project)
        assertEquals(1, viewModel.uiState.generatedLayouts.size)

        val card = viewModel.uiState.generatedLayouts.single()
        assertEquals("result-1", card.nestingResultId)
        assertEquals("Basic T-Shirt", card.patternName)
        assertEquals("M", card.patternSize)
        assertEquals("variant-m", card.variantId)
        assertNull(viewModel.uiState.errorMessage)
    }

    @Test
    fun `loadProject stores not found error when repository returns null`() = runTest {
        val viewModel = ProjectDetailViewModel(
            projectRepository = FakeProjectRepository(),
            patternRepository = FakePatternRepository(),
            nestingResultRepository = FakeNestingResultRepository()
        )

        viewModel.loadProject("missing")
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.isLoading)
        assertEquals("Project not found", viewModel.uiState.errorMessage)
        assertNull(viewModel.uiState.project)
    }

    @Test
    fun `deleteProject does nothing when no project is loaded`() = runTest {
        val projectRepository = FakeProjectRepository()
        val nestingResultRepository = FakeNestingResultRepository()

        val viewModel = ProjectDetailViewModel(
            projectRepository = projectRepository,
            patternRepository = FakePatternRepository(),
            nestingResultRepository = nestingResultRepository
        )

        var deletedCalled = false
        viewModel.deleteProject { deletedCalled = true }
        advanceUntilIdle()

        assertTrue(projectRepository.deletedProjectIds.isEmpty())
        assertTrue(nestingResultRepository.deletedProjectIds.isEmpty())
        assertEquals(false, deletedCalled)
    }

    @Test
    fun `deleteProject deletes nesting results first then project and calls callback`() = runTest {
        val projectRepository = FakeProjectRepository()
        val patternRepository = FakePatternRepository()
        val nestingResultRepository = FakeNestingResultRepository()

        val project = Project(
            id = "project-1",
            name = "My Project",
            fabricPieces = emptyList(),
            rules = PlacementRules(),
            seamAllowance = 1.0
        )
        projectRepository.projectByIdResult = project

        val viewModel = ProjectDetailViewModel(
            projectRepository = projectRepository,
            patternRepository = patternRepository,
            nestingResultRepository = nestingResultRepository
        )

        viewModel.loadProject("project-1")
        advanceUntilIdle()

        var deletedCalled = false
        viewModel.deleteProject { deletedCalled = true }
        advanceUntilIdle()

        assertEquals(
            listOf(
                "deleteNestingResultsByProject:project-1",
                "deleteProject:project-1"
            ),
            nestingResultRepository.callLog.filter { it.startsWith("delete") } +
                    projectRepository.callLog.filter { it.startsWith("delete") }
        )
        assertEquals(true, deletedCalled)
        assertEquals(false, viewModel.uiState.isLoading)
    }

    @Test
    fun `deleteProject stores error message when repository throws`() = runTest {
        val projectRepository = FakeProjectRepository()
        val patternRepository = FakePatternRepository()
        val nestingResultRepository = FakeNestingResultRepository()
        nestingResultRepository.deleteByProjectException = IllegalStateException("Delete failed")

        val project = Project(
            id = "project-1",
            name = "My Project",
            fabricPieces = emptyList(),
            rules = PlacementRules(),
            seamAllowance = 1.0
        )
        projectRepository.projectByIdResult = project

        val viewModel = ProjectDetailViewModel(
            projectRepository = projectRepository,
            patternRepository = patternRepository,
            nestingResultRepository = nestingResultRepository
        )

        viewModel.loadProject("project-1")
        advanceUntilIdle()

        viewModel.deleteProject {}
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.isLoading)
        assertEquals("Delete failed", viewModel.uiState.errorMessage)
    }
}
