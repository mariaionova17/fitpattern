package cz.cvut.fit.bp.projectsPage

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
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProjectsScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shows_loading_state() {
        composeRule.setContent {
            MaterialTheme {
                ProjectsScreen(
                    state = ProjectsUiState(isLoading = true),
                    onCreateProjectClick = {},
                    onProjectClick = {}
                )
            }
        }

        composeRule.onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)
        ).assertExists()
    }

    @Test
    fun shows_empty_state_and_create_button() {
        composeRule.setContent {
            MaterialTheme {
                ProjectsScreen(
                    state = ProjectsUiState(projects = emptyList()),
                    onCreateProjectClick = {},
                    onProjectClick = {}
                )
            }
        }

        composeRule.onNodeWithText("No projects yet").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Create project").assertIsDisplayed()
    }

    @Test
    fun clicking_project_calls_callback_with_project_id() {
        val project = sampleProject(id = "project-42", name = "Tailoring Project")
        var clickedProjectId: String? = null

        composeRule.setContent {
            MaterialTheme {
                ProjectsScreen(
                    state = ProjectsUiState(projects = listOf(project)),
                    onCreateProjectClick = {},
                    onProjectClick = { clickedProjectId = it }
                )
            }
        }

        composeRule.onNodeWithText("Tailoring Project").performClick()

        assertEquals("project-42", clickedProjectId)
    }

    @Test
    fun clicking_create_project_calls_callback() {
        var createClicked = false

        composeRule.setContent {
            MaterialTheme {
                ProjectsScreen(
                    state = ProjectsUiState(projects = listOf(sampleProject())),
                    onCreateProjectClick = { createClicked = true },
                    onProjectClick = {}
                )
            }
        }

        composeRule.onNodeWithContentDescription("Create project").performClick()

        assertEquals(true, createClicked)
    }
}
