package cz.cvut.fit.bp.layoutPage

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cz.cvut.fit.bp.shareddb.data.db.AppDatabase
import cz.cvut.fit.bp.shareddb.data.repository.NestingResultRepositoryImpl
import cz.cvut.fit.bp.shareddb.data.repository.PatternRepositoryImpl
import cz.cvut.fit.bp.shareddb.data.repository.ProjectRepositoryImpl
import cz.cvut.fit.bp.testutils.sampleNestingResult
import cz.cvut.fit.bp.testutils.samplePattern
import cz.cvut.fit.bp.testutils.sampleProject
import cz.cvut.fit.bp.testutils.waitUntil
import domain.solver.BetterSolver
import domain.solver.NestingProblem
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LayoutFlowIntegrationTest {

    private lateinit var db: AppDatabase
    private lateinit var projectRepository: ProjectRepositoryImpl
    private lateinit var patternRepository: PatternRepositoryImpl
    private lateinit var nestingResultRepository: NestingResultRepositoryImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        projectRepository = ProjectRepositoryImpl(db)
        patternRepository = PatternRepositoryImpl(db)
        nestingResultRepository = NestingResultRepositoryImpl(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun openOrGenerateLayout_loads_saved_layout_from_real_database_without_running_solver() {
        val project = sampleProject(id = "project-1", name = "Project One")
        val pattern = samplePattern(
            id = "pattern-1",
            name = "Basic T-Shirt",
            variantId = "pattern-1-variant-m",
            size = "M"
        )
        val savedResult = sampleNestingResult(
            id = "saved-result-1",
            pieceId = "pattern-1-piece-1"
        )

        runBlocking {
            projectRepository.saveProject(project)
            patternRepository.savePattern(pattern)
            nestingResultRepository.saveNestingResult(
                nestingResult = savedResult,
                projectId = project.id,
                patternVariantId = "pattern-1-variant-m"
            )
        }

        val solver = mockk<BetterSolver>(relaxed = true)
        val viewModel = LayoutViewModel(
            patternRepository = patternRepository,
            projectRepository = projectRepository,
            nestingResultRepository = nestingResultRepository,
            solver = solver
        )

        viewModel.openOrGenerateLayout("project-1", "pattern-1-variant-m")

        waitUntil {
            !viewModel.uiState.isLoading && viewModel.uiState.result != null
        }

        assertEquals(project.id, viewModel.uiState.project?.id)
        assertEquals(2, viewModel.uiState.patternPieces.size)
        assertEquals("saved-result-1", viewModel.uiState.result?.id)
        assertEquals(true, viewModel.uiState.hasSavedLayout)
        assertEquals("Basic T-Shirt", viewModel.uiState.patternName)
        assertEquals("M", viewModel.uiState.patternSize)
        assertNull(viewModel.uiState.errorMessage)

        verify(exactly = 0) { solver.solve(any<NestingProblem>(), any()) }
    }

    @Test
    fun openOrGenerateLayout_generates_and_persists_layout_when_no_saved_result_exists() {
        val project = sampleProject(id = "project-1", name = "Project One")
        val pattern = samplePattern(
            id = "pattern-1",
            name = "Basic T-Shirt",
            variantId = "pattern-1-variant-m",
            size = "M"
        )

        runBlocking {
            projectRepository.saveProject(project)
            patternRepository.savePattern(pattern)
        }

        val generatedWithoutIds = sampleNestingResult(
            id = "",
            pieceId = "pattern-1-piece-1"
        )

        val solver = mockk<BetterSolver>()
        every { solver.solve(any<NestingProblem>(), any()) } answers {
            val onProgress = secondArg<(Float) -> Unit>()
            onProgress(0.25f)
            onProgress(1f)
            generatedWithoutIds
        }

        val viewModel = LayoutViewModel(
            patternRepository = patternRepository,
            projectRepository = projectRepository,
            nestingResultRepository = nestingResultRepository,
            solver = solver
        )

        viewModel.openOrGenerateLayout("project-1", "pattern-1-variant-m")

        waitUntil {
            !viewModel.uiState.isLoading &&
                    viewModel.uiState.result != null &&
                    viewModel.uiState.generationProgress == 1f
        }

        val result = viewModel.uiState.result
        assertNotNull(result)
        result!!

        assertTrue(result.id.isNotBlank())
        assertTrue(result.bestLayout.placed.single().id.isNotBlank())
        assertEquals(project.id, viewModel.uiState.project?.id)
        assertEquals(2, viewModel.uiState.patternPieces.size)
        assertEquals(true, viewModel.uiState.hasSavedLayout)
        assertEquals("", viewModel.uiState.patternName)
        assertEquals("M", viewModel.uiState.patternSize)
        assertEquals(1f, viewModel.uiState.generationProgress)
        assertNull(viewModel.uiState.errorMessage)

        val savedResults = runBlocking {
            nestingResultRepository.getNestingResultsByProjectAndVariant(
                "project-1",
                "pattern-1-variant-m"
            )
        }

        assertEquals(1, savedResults.size)
        assertEquals(result.id, savedResults.single().id)

        verify(exactly = 1) { solver.solve(any<NestingProblem>(), any()) }
    }
}
