package cz.cvut.fit.bp.layoutPage

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cz.cvut.fit.bp.testutils.sampleProject
import domain.geometry.Point
import domain.geometry.Shape
import domain.geometry.Polygon
import domain.layout.LayoutState
import domain.layout.Placement
import domain.geometry.Transformation
import domain.pattern.PatternPiece
import domain.solver.NestingResult
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LayoutScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shows_loading_state_with_progress() {
        composeRule.setContent {
            MaterialTheme {
                LayoutScreen(
                    state = LayoutUiState(
                        isLoading = true,
                        generationProgress = 0.4f
                    ),
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo(0.4f, 0f..1f, 0))
        ).assertExists()
        composeRule.onNodeWithText("40%").assertIsDisplayed()
    }

    @Test
    fun shows_error_state() {
        composeRule.setContent {
            MaterialTheme {
                LayoutScreen(
                    state = LayoutUiState(
                        errorMessage = "Generation failed"
                    ),
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithText("Generation failed").assertIsDisplayed()
    }

    @Test
    fun shows_no_layout_data_state() {
        composeRule.setContent {
            MaterialTheme {
                LayoutScreen(
                    state = LayoutUiState(),
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithText("No layout data").assertIsDisplayed()
    }

    @Test
    fun populated_state_shows_project_pattern_and_piece_count() {
        val project = sampleProject(name = "My Layout Project")
        val patternPiece = PatternPiece(
            id = "piece-1",
            name = "Front piece",
            shape = Shape(
                contour = Polygon(
                    listOf(
                        Point(0.0, 0.0),
                        Point(100.0, 0.0),
                        Point(100.0, 120.0),
                        Point(0.0, 120.0)
                    )
                )
            )
        )

        val result = NestingResult(
            id = "result-1",
            bestLayout = LayoutState(
                placed = listOf(
                    Placement(
                        id = "placement-1",
                        pieceId = "piece-1",
                        fabricPieceIndex = 0,
                        transformation = Transformation()
                    )
                )
            )
        )

        composeRule.setContent {
            MaterialTheme {
                LayoutScreen(
                    state = LayoutUiState(
                        project = project,
                        result = result,
                        patternPieces = listOf(patternPiece),
                        patternName = "Basic T-Shirt",
                        patternSize = "M"
                    ),
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithText("Project: My Layout Project").assertIsDisplayed()
        composeRule.onNodeWithText("Pattern: Basic T-Shirt, size: M").assertIsDisplayed()
        composeRule.onNodeWithText("Placed pieces: 1, Not placed: 0").assertIsDisplayed()
        composeRule.onNodeWithText("Fabric 1").assertIsDisplayed()
        composeRule.onNodeWithText("1. Front piece").assertIsDisplayed()
    }

    @Test
    fun clicking_back_triggers_callback() {
        var backClicked = false

        composeRule.setContent {
            MaterialTheme {
                LayoutScreen(
                    state = LayoutUiState(),
                    onBack = { backClicked = true },
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithContentDescription("Back").performClick()

        assertEquals(true, backClicked)
    }
}