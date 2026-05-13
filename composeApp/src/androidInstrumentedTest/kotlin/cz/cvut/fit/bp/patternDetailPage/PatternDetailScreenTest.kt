package cz.cvut.fit.bp.patternDetailPage

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.pattern.Pattern
import domain.pattern.PatternPiece
import domain.pattern.PatternVariant
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PatternDetailScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shows_loading_state() {
        composeRule.setContent {
            MaterialTheme {
                PatternDetailScreen(
                    state = PatternDetailUiState(isLoading = true),
                    onBack = {},
                    onHome = {},
                    onViewLayoutClick = {}
                )
            }
        }

        composeRule.onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)
        ).assertExists()
        composeRule.onNodeWithText("Loading pattern...").assertIsDisplayed()
    }

    @Test
    fun shows_error_state() {
        composeRule.setContent {
            MaterialTheme {
                PatternDetailScreen(
                    state = PatternDetailUiState(errorMessage = "Failed to load pattern"),
                    onBack = {},
                    onHome = {},
                    onViewLayoutClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Failed to load pattern").assertIsDisplayed()
    }

    @Test
    fun shows_pattern_not_found_state() {
        composeRule.setContent {
            MaterialTheme {
                PatternDetailScreen(
                    state = PatternDetailUiState(pattern = null),
                    onBack = {},
                    onHome = {},
                    onViewLayoutClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Pattern not found").assertIsDisplayed()
    }

    @Test
    fun selecting_size_and_clicking_open_layout_uses_selected_variant() {
        val pattern = samplePatternForDetail()
        var selectedVariantId: String? = null

        composeRule.setContent {
            MaterialTheme {
                PatternDetailScreen(
                    state = PatternDetailUiState(pattern = pattern),
                    onBack = {},
                    onHome = {},
                    onViewLayoutClick = { selectedVariantId = it }
                )
            }
        }

        composeRule.onNodeWithText("M").performClick()
        composeRule.onNodeWithText("Open layout").performClick()

        assertEquals("pattern-1-variant-m", selectedVariantId)
    }

    @Test
    fun clicking_back_calls_callback() {
        var backClicked = false

        composeRule.setContent {
            MaterialTheme {
                PatternDetailScreen(
                    state = PatternDetailUiState(pattern = samplePatternForDetail()),
                    onBack = { backClicked = true },
                    onHome = {},
                    onViewLayoutClick = {}
                )
            }
        }

        composeRule.onNodeWithContentDescription("Back").performClick()

        assertEquals(true, backClicked)
    }

    private fun samplePatternForDetail(): Pattern =
        Pattern(
            id = "pattern-1",
            name = "Basic T-Shirt",
            variants = listOf(
                PatternVariant(
                    id = "pattern-1-variant-s",
                    size = "S",
                    pieces = listOf(
                        PatternPiece(
                            id = "piece-s",
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
                ),
                PatternVariant(
                    id = "pattern-1-variant-m",
                    size = "M",
                    pieces = listOf(
                        PatternPiece(
                            id = "piece-m",
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
            pictureUri = "",
            downloadLink = "https://example.com/pattern-1"
        )
}
