package cz.cvut.fit.bp.projectCreation

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProjectCreationScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun clicking_manual_card_opens_manual_dialog_via_callback() {
        var openManualClicked = false

        composeRule.setContent {
            MaterialTheme {
                ProjectCreationScreen(
                    state = ProjectCreationUiState(),
                    onProjectNameChanged = {},
                    onSeamAllowanceChanged = {},
                    onRotationChanged = {},
                    onAllowMirrorChanged = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onWarpChanged = {},
                    onNextClicked = {},
                    onManualDialogDismiss = {},
                    onManualDialogConfirm = {},
                    onOpenManualDialogue = { openManualClicked = true },
                    onOpenSelectImage = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Manually").performClick()

        assertEquals(true, openManualClicked)
    }

    @Test
    fun shows_manual_dialog_when_state_requests_it_and_confirm_calls_callback() {
        var confirmClicked = false

        composeRule.setContent {
            MaterialTheme {
                ProjectCreationScreen(
                    state = ProjectCreationUiState(isManualDialogOpen = true),
                    onProjectNameChanged = {},
                    onSeamAllowanceChanged = {},
                    onRotationChanged = {},
                    onAllowMirrorChanged = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onWarpChanged = {},
                    onNextClicked = {},
                    onManualDialogDismiss = {},
                    onManualDialogConfirm = { confirmClicked = true },
                    onOpenManualDialogue = {},
                    onOpenSelectImage = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Fabric width (mm):").assertIsDisplayed()
        composeRule
            .onAllNodesWithText("Add fabric")
            .filterToOne(hasClickAction())
            .performClick()

        assertEquals(true, confirmClicked)
    }

    @Test
    fun clicking_add_an_image_calls_callback() {
        var openImageClicked = false

        composeRule.setContent {
            MaterialTheme {
                ProjectCreationScreen(
                    state = ProjectCreationUiState(),
                    onProjectNameChanged = {},
                    onSeamAllowanceChanged = {},
                    onRotationChanged = {},
                    onAllowMirrorChanged = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onWarpChanged = {},
                    onNextClicked = {},
                    onManualDialogDismiss = {},
                    onManualDialogConfirm = {},
                    onOpenManualDialogue = {},
                    onOpenSelectImage = { openImageClicked = true },
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Add an image").performClick()

        assertEquals(true, openImageClicked)
    }

    @Test
    fun clicking_next_calls_callback() {
        var nextClicked = false

        composeRule.setContent {
            MaterialTheme {
                ProjectCreationScreen(
                    state = ProjectCreationUiState(),
                    onProjectNameChanged = {},
                    onSeamAllowanceChanged = {},
                    onRotationChanged = {},
                    onAllowMirrorChanged = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onWarpChanged = {},
                    onNextClicked = { nextClicked = true },
                    onManualDialogDismiss = {},
                    onManualDialogConfirm = {},
                    onOpenManualDialogue = {},
                    onOpenSelectImage = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Next").performClick()

        assertEquals(true, nextClicked)
    }

    @Test
    fun toggling_allow_pattern_mirroring_calls_callback() {
        var receivedValue: Boolean? = null

        composeRule.setContent {
            MaterialTheme {
                ProjectCreationScreen(
                    state = ProjectCreationUiState(),
                    onProjectNameChanged = {},
                    onSeamAllowanceChanged = {},
                    onRotationChanged = {},
                    onAllowMirrorChanged = { receivedValue = it },
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onWarpChanged = {},
                    onNextClicked = {},
                    onManualDialogDismiss = {},
                    onManualDialogConfirm = {},
                    onOpenManualDialogue = {},
                    onOpenSelectImage = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNode(
            isToggleable() and hasAnySibling(hasText("Allow pattern mirroring"))
        ).performClick()

        assertEquals(true, receivedValue)
    }

    @Test
    fun clicking_back_calls_callback() {
        var backClicked = false

        composeRule.setContent {
            MaterialTheme {
                ProjectCreationScreen(
                    state = ProjectCreationUiState(),
                    onProjectNameChanged = {},
                    onSeamAllowanceChanged = {},
                    onRotationChanged = {},
                    onAllowMirrorChanged = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onWarpChanged = {},
                    onNextClicked = {},
                    onManualDialogDismiss = {},
                    onManualDialogConfirm = {},
                    onOpenManualDialogue = {},
                    onOpenSelectImage = {},
                    onBack = { backClicked = true }
                )
            }
        }

        composeRule.onNodeWithContentDescription("Back").performClick()

        assertEquals(true, backClicked)
    }

    @Test
    fun entering_project_name_calls_change_callback() {
        var enteredValue: String? = null

        composeRule.setContent {
            MaterialTheme {
                ProjectCreationScreen(
                    state = ProjectCreationUiState(),
                    onProjectNameChanged = { enteredValue = it },
                    onSeamAllowanceChanged = {},
                    onRotationChanged = {},
                    onAllowMirrorChanged = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onWarpChanged = {},
                    onNextClicked = {},
                    onManualDialogDismiss = {},
                    onManualDialogConfirm = {},
                    onOpenManualDialogue = {},
                    onOpenSelectImage = {},
                    onBack = {}
                )
            }
        }

        composeRule.onAllNodes(hasSetTextAction())[0]
            .performSemanticsAction(SemanticsActions.SetText) {
                it(androidx.compose.ui.text.AnnotatedString("My New Project"))
            }

        assertEquals("My New Project", enteredValue)
    }

    @Test
    fun shows_error_message_from_state() {
        composeRule.setContent {
            MaterialTheme {
                ProjectCreationScreen(
                    state = ProjectCreationUiState(errorMessage = "Please add at least one fabric"),
                    onProjectNameChanged = {},
                    onSeamAllowanceChanged = {},
                    onRotationChanged = {},
                    onAllowMirrorChanged = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onWarpChanged = {},
                    onNextClicked = {},
                    onManualDialogDismiss = {},
                    onManualDialogConfirm = {},
                    onOpenManualDialogue = {},
                    onOpenSelectImage = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Please add at least one fabric").assertIsDisplayed()
    }
}
