package cz.cvut.fit.bp.shareddb

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cz.cvut.fit.bp.shareddb.data.db.AppDatabase
import cz.cvut.fit.bp.shareddb.data.repository.PatternRepositoryImpl
import cz.cvut.fit.bp.shareddb.testutils.samplePattern
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
class PatternRepositoryIntegrationTest {

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
    fun savePattern_and_getPatternById_roundTrip_real_database() {
        val pattern = samplePattern(
            id = "pattern-1",
            name = "Basic T-Shirt",
            variantId = "pattern-1-variant-m",
            size = "M"
        )

        runBlocking {
            repository.savePattern(pattern)
        }

        val loaded = runBlocking {
            repository.getPatternById("pattern-1")
        }

        assertNotNull(loaded)
        assertEquals(pattern, loaded)
    }

    @Test
    fun getPatternByVariantId_returns_real_pieces_for_selected_variant() {
        val pattern = samplePattern(
            id = "pattern-1",
            name = "Basic T-Shirt",
            variantId = "pattern-1-variant-m",
            size = "M"
        )

        runBlocking {
            repository.savePattern(pattern)
        }

        val pieces = runBlocking {
            repository.getPatternByVariantId("pattern-1-variant-m")
        }

        assertEquals(2, pieces.size)
        assertEquals(
            listOf("pattern-1-piece-1", "pattern-1-piece-2"),
            pieces.map { it.id }
        )
    }

    @Test
    fun getSizeByVariantId_returns_real_size_from_database() {
        val pattern = samplePattern(
            id = "pattern-1",
            name = "Basic T-Shirt",
            variantId = "pattern-1-variant-m",
            size = "M"
        )

        runBlocking {
            repository.savePattern(pattern)
        }

        val size = runBlocking {
            repository.getSizeByVariantId("pattern-1-variant-m")
        }

        assertEquals("M", size)
    }

    @Test
    fun getAllPatterns_returns_all_saved_patterns() {
        val first = samplePattern(
            id = "pattern-1",
            name = "Basic T-Shirt",
            variantId = "pattern-1-variant-m",
            size = "M"
        )
        val second = samplePattern(
            id = "pattern-2",
            name = "Skirt",
            variantId = "pattern-2-variant-s",
            size = "S"
        )

        runBlocking {
            repository.savePattern(first)
            repository.savePattern(second)
        }

        val allPatterns = runBlocking {
            repository.getAllPatterns()
        }

        assertEquals(2, allPatterns.size)
        assertEquals(
            listOf("Basic T-Shirt", "Skirt"),
            allPatterns.map { it.name }
        )
    }

    @Test
    fun deletePattern_removes_saved_pattern() {
        val pattern = samplePattern(
            id = "pattern-1",
            name = "Basic T-Shirt",
            variantId = "pattern-1-variant-m",
            size = "M"
        )

        runBlocking {
            repository.savePattern(pattern)
        }

        runBlocking {
            repository.deletePattern("pattern-1")
        }

        val loaded = runBlocking {
            repository.getPatternById("pattern-1")
        }
        val allPatterns = runBlocking {
            repository.getAllPatterns()
        }

        assertNull(loaded)
        assertTrue(allPatterns.isEmpty())
    }
}
