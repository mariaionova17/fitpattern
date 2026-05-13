package cz.cvut.fit.bp.patternSuggestionsPage

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

class PatternSuggestionsScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shows_loading_state() {
        composeRule.setContent {
            MaterialTheme {
                PatternSuggestionsScreen(
                    state = PatternSuggestionsUiState(isLoading = true),
                    onPatternClick = {},
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)
        ).assertExists()
    }

    @Test
    fun shows_error_state() {
        composeRule.setContent {
            MaterialTheme {
                PatternSuggestionsScreen(
                    state = PatternSuggestionsUiState(errorMessage = "Failed to load patterns"),
                    onPatternClick = {},
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithText("Failed to load patterns").assertIsDisplayed()
    }

    @Test
    fun shows_empty_state() {
        composeRule.setContent {
            MaterialTheme {
                PatternSuggestionsScreen(
                    state = PatternSuggestionsUiState(patterns = emptyList()),
                    onPatternClick = {},
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithText("No patterns yet").assertIsDisplayed()
    }

    @Test
    fun clicking_pattern_calls_callback_with_pattern_id() {
        val pattern = samplePattern(id = "pattern-42", name = "Summer Dress")
        var clickedPatternId: String? = null

        composeRule.setContent {
            MaterialTheme {
                PatternSuggestionsScreen(
                    state = PatternSuggestionsUiState(patterns = listOf(pattern)),
                    onPatternClick = { clickedPatternId = it },
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithText("Summer Dress").performClick()

        assertEquals("pattern-42", clickedPatternId)
    }

    @Test
    fun clicking_back_calls_callback() {
        var backClicked = false

        composeRule.setContent {
            MaterialTheme {
                PatternSuggestionsScreen(
                    state = PatternSuggestionsUiState(patterns = emptyList()),
                    onPatternClick = {},
                    onBack = { backClicked = true },
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithContentDescription("Back").performClick()

        assertEquals(true, backClicked)
    }

    private fun samplePattern(id: String, name: String): Pattern =
        Pattern(
            id = id,
            name = name,
            variants = listOf(
                PatternVariant(
                    id = "$id-variant-s",
                    size = "S",
                    pieces = listOf(
                        PatternPiece(
                            id = "$id-piece",
                            name = "Front",
                            shape = Shape(
                                contour = Polygon(
                                    listOf(
                                        Point(0.0, 0.0),
                                        Point(10.0, 0.0),
                                        Point(10.0, 10.0),
                                        Point(0.0, 10.0)
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            pictureUri = "",
            downloadLink = ""
        )
}