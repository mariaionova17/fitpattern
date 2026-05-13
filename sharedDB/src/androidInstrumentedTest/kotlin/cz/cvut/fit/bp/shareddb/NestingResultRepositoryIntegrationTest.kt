package cz.cvut.fit.bp.shareddb

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cz.cvut.fit.bp.shareddb.data.db.AppDatabase
import cz.cvut.fit.bp.shareddb.data.repository.NestingResultRepositoryImpl
import cz.cvut.fit.bp.shareddb.data.repository.PatternRepositoryImpl
import cz.cvut.fit.bp.shareddb.data.repository.ProjectRepositoryImpl
import cz.cvut.fit.bp.shareddb.testutils.sampleNestingResult
import cz.cvut.fit.bp.shareddb.testutils.samplePattern
import cz.cvut.fit.bp.shareddb.testutils.sampleProject
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NestingResultRepositoryIntegrationTest {

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
    fun saveNestingResult_assigns_incrementing_versions_and_returns_results_in_order() {
        val project = sampleProject(id = "project-1", name = "Project One")
        val pattern = samplePattern(
            id = "pattern-1",
            name = "Basic T-Shirt",
            variantId = "pattern-1-variant-m",
            size = "M"
        )
        val firstResult = sampleNestingResult(id = "result-1", pieceId = "pattern-1-piece-1")
        val secondResult = sampleNestingResult(id = "result-2", pieceId = "pattern-1-piece-2")

        runBlocking {
            projectRepository.saveProject(project)
            patternRepository.savePattern(pattern)

            nestingResultRepository.saveNestingResult(
                nestingResult = firstResult,
                projectId = project.id,
                patternVariantId = "pattern-1-variant-m"
            )
            nestingResultRepository.saveNestingResult(
                nestingResult = secondResult,
                projectId = project.id,
                patternVariantId = "pattern-1-variant-m"
            )
        }

        val results = runBlocking {
            nestingResultRepository.getNestingResultsByProjectAndVariant(
                project.id,
                "pattern-1-variant-m"
            )
        }

        assertEquals(2, results.size)
        assertEquals(listOf("result-1", "result-2"), results.map { it.id })
    }

    @Test
    fun getPatternNameAndVariantIdFromNestingResult_resolves_real_joined_data() {
        val project = sampleProject(id = "project-1", name = "Project One")
        val pattern = samplePattern(
            id = "pattern-1",
            name = "Basic T-Shirt",
            variantId = "pattern-1-variant-m",
            size = "M"
        )
        val result = sampleNestingResult(id = "result-1", pieceId = "pattern-1-piece-1")

        runBlocking {
            projectRepository.saveProject(project)
            patternRepository.savePattern(pattern)
            nestingResultRepository.saveNestingResult(
                nestingResult = result,
                projectId = project.id,
                patternVariantId = "pattern-1-variant-m"
            )
        }

        val resolved = runBlocking {
            nestingResultRepository.getPatternNameAndVariantIdFromNestingResult("result-1")
        }

        assertEquals("Basic T-Shirt" to "pattern-1-variant-m", resolved)
    }

    @Test
    fun deleteNestingResultsByProject_removes_all_saved_results_for_that_project() {
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
            nestingResultRepository.saveNestingResult(
                nestingResult = sampleNestingResult(id = "result-1", pieceId = "pattern-1-piece-1"),
                projectId = project.id,
                patternVariantId = "pattern-1-variant-m"
            )
            nestingResultRepository.saveNestingResult(
                nestingResult = sampleNestingResult(id = "result-2", pieceId = "pattern-1-piece-2"),
                projectId = project.id,
                patternVariantId = "pattern-1-variant-m"
            )
        }

        runBlocking {
            nestingResultRepository.deleteNestingResultsByProject(project.id)
        }

        val remaining = runBlocking {
            nestingResultRepository.getNestingResultsByProject(project.id)
        }

        assertTrue(remaining.isEmpty())
    }
}
