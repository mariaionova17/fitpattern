package cz.cvut.fit.bp.patternDetailPage

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cz.cvut.fit.bp.patternSuggestionsPage.PatternSuggestionsViewModel
import cz.cvut.fit.bp.shareddb.data.db.AppDatabase
import cz.cvut.fit.bp.shareddb.data.repository.PatternRepositoryImpl
import cz.cvut.fit.bp.testutils.samplePattern
import cz.cvut.fit.bp.testutils.waitUntil
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PatternBrowsingIntegrationTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: PatternRepositoryImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repository = PatternRepositoryImpl(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun patternSuggestionsViewModel_loads_patterns_from_real_database() {
        runBlocking {
            repository.savePattern(samplePattern(id = "pattern-1", name = "Basic T-Shirt", variantId = "pattern-1-variant-m"))
            repository.savePattern(samplePattern(id = "pattern-2", name = "Skirt", variantId = "pattern-2-variant-s", size = "S"))
        }

        val viewModel = PatternSuggestionsViewModel(repository)

        viewModel.loadPatterns()

        waitUntil {
            !viewModel.uiState.isLoading && viewModel.uiState.patterns.size == 2
        }

        assertEquals(2, viewModel.uiState.patterns.size)
        assertEquals(
            listOf("Basic T-Shirt", "Skirt"),
            viewModel.uiState.patterns.map { it.name }
        )
        assertNull(viewModel.uiState.errorMessage)
    }

    @Test
    fun patternDetailViewModel_loads_selected_pattern_from_real_database() {
        val pattern = samplePattern(
            id = "pattern-1",
            name = "Basic T-Shirt",
            variantId = "pattern-1-variant-m"
        )

        runBlocking {
            repository.savePattern(pattern)
        }

        val viewModel = PatternDetailViewModel(repository)

        viewModel.loadPattern("pattern-1")

        waitUntil {
            !viewModel.uiState.isLoading && viewModel.uiState.pattern != null
        }

        val loaded = viewModel.uiState.pattern
        assertEquals(pattern.id, loaded?.id)
        assertEquals(pattern.name, loaded?.name)
        assertEquals(1, loaded?.variants?.size)
        assertEquals("pattern-1-variant-m", loaded?.variants?.single()?.id)
        assertEquals(2, loaded?.variants?.single()?.pieces?.size)
        assertNull(viewModel.uiState.errorMessage)
    }
}
