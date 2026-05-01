package cz.cvut.fit.bp.patternSuggestionsPage

import cz.cvut.fit.bp.FakePatternRepository
import cz.cvut.fit.bp.testutil.MainDispatcherRule
import domain.pattern.Pattern
import domain.pattern.PatternVariant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class PatternSuggestionsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadPatterns stores loaded patterns and clears loading`() = runTest {
        val repository = FakePatternRepository()
        val patterns = listOf(
            Pattern(
                id = "pattern-1",
                name = "Dress",
                variants = listOf(PatternVariant("variant-s", "S", emptyList())),
                pictureUri = "content://dress",
                downloadLink = "https://example.com/dress"
            ),
            Pattern(
                id = "pattern-2",
                name = "Skirt",
                variants = listOf(PatternVariant("variant-m", "M", emptyList())),
                pictureUri = "content://skirt",
                downloadLink = "https://example.com/skirt"
            )
        )
        repository.allPatternsResult = patterns

        val viewModel = PatternSuggestionsViewModel(repository)

        viewModel.loadPatterns()
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.isLoading)
        assertEquals(patterns, viewModel.uiState.patterns)
        assertNull(viewModel.uiState.errorMessage)
    }

    @Test
    fun `loadPatterns stores error when repository throws`() = runTest {
        val repository = FakePatternRepository()
        repository.getAllPatternsException = IllegalStateException("Suggestions failed")

        val viewModel = PatternSuggestionsViewModel(repository)

        viewModel.loadPatterns()
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.isLoading)
        assertEquals(emptyList(), viewModel.uiState.patterns)
        assertEquals("Suggestions failed", viewModel.uiState.errorMessage)
    }
}
