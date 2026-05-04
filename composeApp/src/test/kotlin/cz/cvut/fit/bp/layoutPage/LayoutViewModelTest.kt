package cz.cvut.fit.bp.layoutPage

import cz.cvut.fit.bp.testutil.MainDispatcherRule
import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.layout.LayoutScore
import domain.layout.LayoutState
import domain.layout.Placement
import domain.pattern.PatternPiece
import domain.pattern.PlacementRules
import domain.project.Project
import domain.repository.NestingResultRepository
import domain.repository.PatternRepository
import domain.repository.ProjectRepository
import domain.solver.BetterSolver
import domain.solver.NestingProblem
import domain.solver.NestingResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LayoutViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val patternRepository = mockk<PatternRepository>()
    private val projectRepository = mockk<ProjectRepository>()
    private val nestingResultRepository = mockk<NestingResultRepository>()
    private val solver = mockk<BetterSolver>()

    @Test
    fun `openOrGenerateLayout stores error when project is not found`() = runTest {
        coEvery { projectRepository.getProjectById("project-1") } returns null

        val viewModel = createViewModel()

        viewModel.openOrGenerateLayout("project-1", "variant-m")
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.isLoading)
        assertEquals(0f, viewModel.uiState.generationProgress)
        assertEquals("Project not found", viewModel.uiState.errorMessage)
        assertNull(viewModel.uiState.project)
        assertNull(viewModel.uiState.result)

        coVerify(exactly = 1) { projectRepository.getProjectById("project-1") }
        verify(exactly = 0) { solver.solve(any(), any()) }
    }

    @Test
    fun `openOrGenerateLayout loads saved layout without running solver`() = runTest {
        val project = sampleProject("project-1")
        val pieces = listOf(samplePiece("piece-1"))
        val savedResult = sampleResult(
            resultId = "saved-result-1",
            placementId = "saved-placement-1"
        )

        coEvery { projectRepository.getProjectById("project-1") } returns project
        coEvery { patternRepository.getPatternByVariantId("variant-m") } returns pieces
        coEvery { patternRepository.getSizeByVariantId("variant-m") } returns "M"
        coEvery {
            nestingResultRepository.getNestingResultsByProjectAndVariant("project-1", "variant-m")
        } returns listOf(savedResult)
        coEvery {
            nestingResultRepository.getPatternNameAndVariantIdFromNestingResult("saved-result-1")
        } returns ("Basic T-Shirt" to "variant-m")

        val viewModel = createViewModel()

        viewModel.openOrGenerateLayout("project-1", "variant-m")
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.isLoading)
        assertEquals(project, viewModel.uiState.project)
        assertEquals(pieces, viewModel.uiState.patternPieces)
        assertEquals(savedResult, viewModel.uiState.result)
        assertEquals(true, viewModel.uiState.hasSavedLayout)
        assertEquals("Basic T-Shirt", viewModel.uiState.patternName)
        assertEquals("M", viewModel.uiState.patternSize)
        assertNull(viewModel.uiState.errorMessage)

        verify(exactly = 0) { solver.solve(any(), any()) }
        coVerify(exactly = 0) {
            nestingResultRepository.saveNestingResult(any(), any(), any())
        }
    }

    @Test
    fun `openOrGenerateLayout generates layout saves it and updates progress when no saved result exists`() = runTest {
        val project = sampleProject("project-1")
        val pieces = listOf(samplePiece("piece-1"))
        val generatedWithoutIds = sampleResult(
            resultId = "",
            placementId = ""
        )

        coEvery { projectRepository.getProjectById("project-1") } returns project
        coEvery { patternRepository.getPatternByVariantId("variant-m") } returns pieces
        coEvery { patternRepository.getSizeByVariantId("variant-m") } returns "M"
        coEvery {
            nestingResultRepository.getNestingResultsByProjectAndVariant("project-1", "variant-m")
        } returns emptyList()

        every { solver.solve(any<NestingProblem>(), any()) } answers {
            val progressCallback = secondArg<(Float) -> Unit>()
            progressCallback(0.25f)
            progressCallback(1f)
            generatedWithoutIds
        }

        val savedResultSlot = slot<NestingResult>()
        coEvery {
            nestingResultRepository.saveNestingResult(
                capture(savedResultSlot),
                "project-1",
                "variant-m"
            )
        } returns Unit

        coEvery {
            nestingResultRepository.getPatternNameAndVariantIdFromNestingResult(any())
        } returns ("Basic T-Shirt" to "variant-m")

        val viewModel = createViewModel()

        viewModel.openOrGenerateLayout("project-1", "variant-m")
        advanceUntilIdle()

        val savedResult = savedResultSlot.captured

        assertEquals(false, viewModel.uiState.isLoading)
        assertEquals(project, viewModel.uiState.project)
        assertEquals(pieces, viewModel.uiState.patternPieces)
        assertEquals(true, viewModel.uiState.hasSavedLayout)
        assertEquals("Basic T-Shirt", viewModel.uiState.patternName)
        assertEquals("M", viewModel.uiState.patternSize)
        assertEquals(1f, viewModel.uiState.generationProgress)
        assertNull(viewModel.uiState.errorMessage)

        assertTrue(savedResult.id.isNotBlank())
        assertTrue(savedResult.bestLayout.placed.single().id.isNotBlank())
        assertEquals(savedResult, viewModel.uiState.result)

        verify(exactly = 1) { solver.solve(any(), any()) }
        coVerify(exactly = 1) {
            nestingResultRepository.saveNestingResult(any(), "project-1", "variant-m")
        }
    }

    @Test
    fun `openOrGenerateLayout stores error when repository throws`() = runTest {
        coEvery { projectRepository.getProjectById("project-1") } throws IllegalStateException("Project load failed")

        val viewModel = createViewModel()

        viewModel.openOrGenerateLayout("project-1", "variant-m")
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.isLoading)
        assertEquals("Project load failed", viewModel.uiState.errorMessage)
        assertNull(viewModel.uiState.project)
        assertNull(viewModel.uiState.result)
    }

    private fun createViewModel(): LayoutViewModel =
        LayoutViewModel(
            patternRepository = patternRepository,
            projectRepository = projectRepository,
            nestingResultRepository = nestingResultRepository,
            solver = solver,
            solverDispatcher = mainDispatcherRule.dispatcher
        )

    private fun sampleProject(id: String): Project =
        Project(
            id = id,
            name = "My Project",
            fabricPieces = emptyList(),
            rules = PlacementRules(),
            seamAllowance = 1.5
        )

    private fun samplePiece(id: String): PatternPiece =
        PatternPiece(
            id = id,
            name = "Front",
            shape = Shape(
                contour = Polygon(
                    listOf(
                        Point(0.0, 0.0),
                        Point(10.0, 0.0),
                        Point(10.0, 5.0),
                        Point(0.0, 5.0)
                    )
                )
            )
        )

    private fun sampleResult(resultId: String, placementId: String): NestingResult =
        NestingResult(
            id = resultId,
            bestLayout = LayoutState(
                placed = listOf(
                    Placement(
                        id = placementId,
                        pieceId = "piece-1",
                        fabricPieceIndex = 0,
                        transformation = domain.geometry.Transformation()
                    )
                ),
                unplaced = emptyList(),
                score = LayoutScore(100.0, 50.0)
            ),
            triedLayouts = 10,
            timeMs = 123
        )
}
