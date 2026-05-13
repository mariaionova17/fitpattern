package cz.cvut.fit.bp.projectCreation

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cz.cvut.fit.bp.shareddb.data.db.AppDatabase
import cz.cvut.fit.bp.shareddb.data.repository.ProjectRepositoryImpl
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking

@RunWith(AndroidJUnit4::class)
class ProjectCreationFlowIntegrationTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: ProjectRepositoryImpl
    private lateinit var viewModel: ProjectCreationViewModel

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
        viewModel = ProjectCreationViewModel(repository)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun saveProject_persists_manual_project_into_real_database() {
        var savedProjectId: String? = null
        val latch = CountDownLatch(1)

        viewModel.onProjectNameChanged("Integration Test Project")
        viewModel.onSeamAllowanceChanged("12")
        viewModel.onRotationChanged(UiRotationOption.BY180)
        viewModel.onAllowMirrorChanged(true)

        viewModel.openManualDialog()
        viewModel.onFabricWidthChanged("1500")
        viewModel.onFabricLengthChanged("2500")
        viewModel.onWarpChanged(true)
        viewModel.confirmManualDialog()

        assertTrue(viewModel.uiState.addedFabricPieces.isNotEmpty())
        assertNull(viewModel.uiState.errorMessage)

        viewModel.saveProject { id ->
            savedProjectId = id
            latch.countDown()
        }

        assertTrue("saveProject callback was not invoked in time", latch.await(3, TimeUnit.SECONDS))
        assertNotNull(savedProjectId)
        assertNull(viewModel.uiState.errorMessage)

        val saved = runBlocking {
            repository.getProjectById(savedProjectId!!)
        }

        assertNotNull(saved)
        saved!!

        assertEquals("Integration Test Project", saved.name)
        assertEquals(12.0, saved.seamAllowance, 0.0001)
        assertEquals(true, saved.rules.allowMirror)
        assertEquals(listOf(0.0, 180.0), saved.rules.rotation.allowedAnglesDeg)
        assertEquals(1, saved.fabricPieces.size)
    }
}