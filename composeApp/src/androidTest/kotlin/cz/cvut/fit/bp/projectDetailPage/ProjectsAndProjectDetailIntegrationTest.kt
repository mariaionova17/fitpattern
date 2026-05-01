package cz.cvut.fit.bp.projectDetailPage

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cz.cvut.fit.bp.projectsPage.ProjectsViewModel
import cz.cvut.fit.bp.shareddb.data.db.AppDatabase
import cz.cvut.fit.bp.shareddb.data.repository.NestingResultRepositoryImpl
import cz.cvut.fit.bp.shareddb.data.repository.PatternRepositoryImpl
import cz.cvut.fit.bp.shareddb.data.repository.ProjectRepositoryImpl
import cz.cvut.fit.bp.testutils.sampleNestingResult
import cz.cvut.fit.bp.testutils.samplePattern
import cz.cvut.fit.bp.testutils.sampleProject
import cz.cvut.fit.bp.testutils.waitUntil
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ProjectsAndProjectDetailIntegrationTest {

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
    fun projectsViewModel_loads_saved_projects_from_real_database() {
        runBlocking {
            projectRepository.saveProject(sampleProject(id = "project-1", name = "Project One"))
            projectRepository.saveProject(sampleProject(id = "project-2", name = "Project Two"))
        }

        val viewModel = ProjectsViewModel(projectRepository)

        viewModel.loadProjects()

        waitUntil {
            !viewModel.uiState.isLoading && viewModel.uiState.projects.size == 2
        }

        assertEquals(2, viewModel.uiState.projects.size)
        assertEquals(
            listOf("Project One", "Project Two"),
            viewModel.uiState.projects.map { it.name }
        )
        assertNull(viewModel.uiState.errorMessage)
    }

    @Test
    fun projectDetailViewModel_loads_generated_layout_metadata_and_deletes_project_with_results() {
        val project = sampleProject(id = "project-1", name = "Project One")
        val pattern = samplePattern(
            id = "pattern-1",
            name = "Basic T-Shirt",
            variantId = "pattern-1-variant-m",
            size = "M"
        )
        val result = sampleNestingResult(
            id = "result-1",
            pieceId = "pattern-1-piece-1"
        )

        runBlocking {
            projectRepository.saveProject(project)
            patternRepository.savePattern(pattern)
            nestingResultRepository.saveNestingResult(
                nestingResult = result,
                projectId = project.id,
                patternVariantId = "pattern-1-variant-m"
            )
        }

        val viewModel = ProjectDetailViewModel(
            projectRepository = projectRepository,
            patternRepository = patternRepository,
            nestingResultRepository = nestingResultRepository
        )

        viewModel.loadProject(project.id)

        waitUntil {
            !viewModel.uiState.isLoading && viewModel.uiState.project != null
        }

        assertEquals(project.id, viewModel.uiState.project?.id)
        assertEquals(1, viewModel.uiState.generatedLayouts.size)

        val card = viewModel.uiState.generatedLayouts.single()
        assertEquals("result-1", card.nestingResultId)
        assertEquals("Basic T-Shirt", card.patternName)
        assertEquals("M", card.patternSize)
        assertEquals("pattern-1-variant-m", card.variantId)
        assertNull(viewModel.uiState.errorMessage)

        var deleteCallbackCalled = false
        val latch = CountDownLatch(1)

        viewModel.deleteProject {
            deleteCallbackCalled = true
            latch.countDown()
        }

        assertTrue("deleteProject callback was not invoked in time", latch.await(3, TimeUnit.SECONDS))
        assertTrue(deleteCallbackCalled)
        assertEquals(false, viewModel.uiState.isLoading)

        val deletedProject = runBlocking {
            projectRepository.getProjectById(project.id)
        }
        val remainingResults = runBlocking {
            nestingResultRepository.getNestingResultsByProject(project.id)
        }

        assertNull(deletedProject)
        assertTrue(remainingResults.isEmpty())
    }
}
