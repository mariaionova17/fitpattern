package cz.cvut.fit.bp.patternDetailPage

import cz.cvut.fit.bp.FakePatternRepository
import cz.cvut.fit.bp.testutil.MainDispatcherRule
import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.pattern.Pattern
import domain.pattern.PatternPiece
import domain.pattern.PatternVariant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class PatternDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadPattern stores loaded pattern and clears loading`() = runTest {
        val repository = FakePatternRepository()
        val pattern = samplePattern("pattern-1")
        repository.patternByIdResult = pattern

        val viewModel = PatternDetailViewModel(repository)

        viewModel.loadPattern("pattern-1")
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.isLoading)
        assertEquals(pattern, viewModel.uiState.pattern)
        assertNull(viewModel.uiState.errorMessage)
    }

    @Test
    fun `loadPattern stores error message when repository throws`() = runTest {
        val repository = FakePatternRepository()
        repository.getPatternByIdException = IllegalStateException("Pattern load failed")

        val viewModel = PatternDetailViewModel(repository)

        viewModel.loadPattern("pattern-1")
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.isLoading)
        assertEquals("Pattern load failed", viewModel.uiState.errorMessage)
        assertNull(viewModel.uiState.pattern)
    }

    private fun samplePattern(id: String): Pattern =
        Pattern(
            id = id,
            name = "Basic T-Shirt",
            variants = listOf(
                PatternVariant(
                    id = "variant-m",
                    size = "M",
                    pieces = listOf(
                        PatternPiece(
                            id = "piece-1",
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
                    )
                )
            ),
            pictureUri = "content://pattern",
            downloadLink = "https://example.com/pattern"
        )
}
