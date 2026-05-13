package cz.cvut.fit.bp.shareddb

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cz.cvut.fit.bp.shareddb.data.db.AppDatabase
import cz.cvut.fit.bp.shareddb.data.entity.LayoutPlacedPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.NestingResultEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternVariantEntity
import cz.cvut.fit.bp.shareddb.data.repository.ProjectRepositoryImpl
import cz.cvut.fit.bp.shareddb.testutils.sampleProject
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
class ProjectRepositoryIntegrationTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: ProjectRepositoryImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repository = ProjectRepositoryImpl(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun saveProject_and_getProjectById_roundTrip_real_database() {
        val project = sampleProject(
            id = "project-1",
            name = "Repository Project"
        )

        runBlocking {
            repository.saveProject(project)
        }

        val loaded = runBlocking {
            repository.getProjectById("project-1")
        }

        assertNotNull(loaded)
        assertEquals(project, loaded)
    }

    @Test
    fun getAllProjects_returns_all_saved_projects() {
        val first = sampleProject(id = "project-1", name = "Project One")
        val second = sampleProject(id = "project-2", name = "Project Two")

        runBlocking {
            repository.saveProject(first)
            repository.saveProject(second)
        }

        val allProjects = runBlocking {
            repository.getAllProjects()
        }

        assertEquals(2, allProjects.size)
        assertEquals(
            listOf("Project One", "Project Two"),
            allProjects.map { it.name }
        )
    }

    @Test
    fun saveProject_replaces_existing_project_data_for_same_id() {
        val original = sampleProject(id = "project-1", name = "Original Project")
        val updated = sampleProject(id = "project-1", name = "Updated Project").copy(
            seamAllowance = 7.5,
            fabricPieces = listOf(
                original.fabricPieces.first().copy(id = "project-1-fabric-updated")
            )
        )

        runBlocking {
            repository.saveProject(original)
            repository.saveProject(updated)
        }

        val loaded = runBlocking {
            repository.getProjectById("project-1")
        }

        assertNotNull(loaded)
        loaded!!

        assertEquals("Updated Project", loaded.name)
        assertEquals(7.5, loaded.seamAllowance, 0.0001)
        assertEquals(1, loaded.fabricPieces.size)
        assertEquals("project-1-fabric-updated", loaded.fabricPieces.single().id)
    }

    @Test
    fun deleteProject_removes_saved_project() {
        val project = sampleProject(id = "project-1", name = "Delete Me")

        runBlocking {
            repository.saveProject(project)
        }

        runBlocking {
            repository.deleteProject("project-1")
        }

        val loaded = runBlocking {
            repository.getProjectById("project-1")
        }
        val allProjects = runBlocking {
            repository.getAllProjects()
        }

        assertNull(loaded)
        assertTrue(allProjects.isEmpty())
    }

    @Test
    fun deleteProject_removes_related_rules_and_fabrics() {
        val project = sampleProject(id = "project-1", name = "Cascade Project")

        runBlocking {
            repository.saveProject(project)
        }

        val rulesBeforeDelete = runBlocking {
            db.projectDao().getRulesByProjectId("project-1")
        }
        val fabricsBeforeDelete = runBlocking {
            db.projectDao().getFabricsByProjectId("project-1")
        }

        assertNotNull(rulesBeforeDelete)
        assertTrue(fabricsBeforeDelete.isNotEmpty())

        runBlocking {
            repository.deleteProject("project-1")
        }

        val projectAfterDelete = runBlocking {
            repository.getProjectById("project-1")
        }
        val rulesAfterDelete = runBlocking {
            db.projectDao().getRulesByProjectId("project-1")
        }
        val fabricsAfterDelete = runBlocking {
            db.projectDao().getFabricsByProjectId("project-1")
        }

        assertNull(projectAfterDelete)
        assertNull(rulesAfterDelete)
        assertTrue(fabricsAfterDelete.isEmpty())
    }

    @Test
    fun deleteProject_removes_related_nesting_results_and_placed_pieces() {
        val project = sampleProject(id = "project-1", name = "Project With Layout")

        runBlocking {
            repository.saveProject(project)

            db.patternDao().insertPattern(
                PatternEntity(
                    id = "pattern-1",
                    name = "Test Pattern",
                    pictureUri = "",
                    downloadLink = ""
                )
            )

            db.patternDao().insertVariants(
                listOf(
                    PatternVariantEntity(
                        variantId = "variant-1",
                        patternId = "pattern-1",
                        size = "M"
                    )
                )
            )

            db.nestingResultDao().insertNestingResult(
                NestingResultEntity(
                    id = "result-1",
                    projectId = "project-1",
                    patternVariantId = "variant-1",
                    layoutVersionIndex = 1
                )
            )

            db.nestingResultDao().insertPlacedPieces(
                listOf(
                    LayoutPlacedPieceEntity(
                        id = "placed-1",
                        nestingResultId = "result-1",
                        pieceId = "piece-1",
                        fabricPieceIndex = 0,
                        transformationJson = "{\"move\":{\"x\":0.0,\"y\":0.0}}"
                    )
                )
            )
        }

        val resultsBeforeDelete = runBlocking {
            db.nestingResultDao().getResultsForProject("project-1")
        }
        val resultWithPiecesBeforeDelete = runBlocking {
            db.nestingResultDao().getNestingResultWithPieces("result-1")
        }

        assertEquals(1, resultsBeforeDelete.size)
        assertNotNull(resultWithPiecesBeforeDelete)
        assertEquals(1, resultWithPiecesBeforeDelete!!.placedPieces.size)

        runBlocking {
            repository.deleteProject("project-1")
        }

        val projectAfterDelete = runBlocking {
            repository.getProjectById("project-1")
        }
        val resultsAfterDelete = runBlocking {
            db.nestingResultDao().getResultsForProject("project-1")
        }
        val resultWithPiecesAfterDelete = runBlocking {
            db.nestingResultDao().getNestingResultWithPieces("result-1")
        }

        assertNull(projectAfterDelete)
        assertTrue(resultsAfterDelete.isEmpty())
        assertNull(resultWithPiecesAfterDelete)
    }
}
