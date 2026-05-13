package cz.cvut.fit.bp.projectDetailPage

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cz.cvut.fit.bp.testutils.sampleProject
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProjectDetailScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shows_project_info() {
        val project = sampleProject(name = "Tailoring Project")

        composeRule.setContent {
            MaterialTheme {
                ProjectDetailScreen(
                    state = ProjectDetailUiState(project = project),
                    onViewPatternSuggestionsClick = {},
                    onGeneratedLayoutClick = { _, _ -> },
                    onDeleteProjectClick = {},
                    onOpenEditProjectDialog = {},
                    onCloseEditProjectDialog = {},
                    onEditedProjectNameChanged = {},
                    onEditedSeamAllowanceChanged ={},
                    onEditedRotationChanged = {},
                    onEditedAllowMirrorChanged = {},
                    onSaveEditedProject = {},
                    onCloseDeleteLayoutsAfterEditDialog = {},
                    onConfirmSaveEditedProjectWithLayoutDeletion = {},
                    onOpenFabricOptionsDialog = {},
                    onCloseFabricOptionsDialog = {},
                    onStartManualFabricAdd = {},
                    onStartImageFabricAdd = {},
                    onCloseManualFabricDialog = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onFabricWarpChanged = {},
                    onSaveManualFabricDraft = {},
                    onRequestDeleteFabric = {},
                    onCloseDeleteLayoutsAfterFabricChangeDialog = {},
                    onConfirmPendingFabricMutationWithLayoutDeletion = {},
                    onOpenRenameFabricDialog = {},
                    onCloseRenameFabricDialog = {},
                    onFabricNameChanged = {},
                    onSaveRenamedFabric = {},
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithText("Project information").assertIsDisplayed()
        composeRule.onAllNodesWithText("Tailoring Project").assertCountEquals(2)
        composeRule.onNodeWithText("Seam allowance").assertIsDisplayed()
        composeRule.onNodeWithText("Fabric count").assertIsDisplayed()
        composeRule.onNodeWithText("Choose a pattern").assertIsDisplayed()
    }

    @Test
    fun shows_loading_state() {
        composeRule.setContent {
            MaterialTheme {
                ProjectDetailScreen(
                    state = ProjectDetailUiState(isLoading = true),
                    onViewPatternSuggestionsClick = {},
                    onGeneratedLayoutClick = { _, _ -> },
                    onDeleteProjectClick = {},
                    onOpenEditProjectDialog = {},
                    onCloseEditProjectDialog = {},
                    onEditedProjectNameChanged = {},
                    onEditedSeamAllowanceChanged ={},
                    onEditedRotationChanged = {},
                    onEditedAllowMirrorChanged = {},
                    onSaveEditedProject = {},
                    onCloseDeleteLayoutsAfterEditDialog = {},
                    onConfirmSaveEditedProjectWithLayoutDeletion = {},
                    onOpenFabricOptionsDialog = {},
                    onCloseFabricOptionsDialog = {},
                    onStartManualFabricAdd = {},
                    onStartImageFabricAdd = {},
                    onCloseManualFabricDialog = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onFabricWarpChanged = {},
                    onSaveManualFabricDraft = {},
                    onRequestDeleteFabric = {},
                    onCloseDeleteLayoutsAfterFabricChangeDialog = {},
                    onConfirmPendingFabricMutationWithLayoutDeletion = {},
                    onOpenRenameFabricDialog = {},
                    onCloseRenameFabricDialog = {},
                    onFabricNameChanged = {},
                    onSaveRenamedFabric = {},
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithText("Loading project...").assertIsDisplayed()
    }

    @Test
    fun shows_error_state() {
        composeRule.setContent {
            MaterialTheme {
                ProjectDetailScreen(
                    state = ProjectDetailUiState(errorMessage = "Project loading failed"),
                    onViewPatternSuggestionsClick = {},
                    onGeneratedLayoutClick = { _, _ -> },
                    onDeleteProjectClick = {},
                    onOpenEditProjectDialog = {},
                    onCloseEditProjectDialog = {},
                    onEditedProjectNameChanged = {},
                    onEditedSeamAllowanceChanged ={},
                    onEditedRotationChanged = {},
                    onEditedAllowMirrorChanged = {},
                    onSaveEditedProject = {},
                    onCloseDeleteLayoutsAfterEditDialog = {},
                    onConfirmSaveEditedProjectWithLayoutDeletion = {},
                    onOpenFabricOptionsDialog = {},
                    onCloseFabricOptionsDialog = {},
                    onStartManualFabricAdd = {},
                    onStartImageFabricAdd = {},
                    onCloseManualFabricDialog = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onFabricWarpChanged = {},
                    onSaveManualFabricDraft = {},
                    onRequestDeleteFabric = {},
                    onCloseDeleteLayoutsAfterFabricChangeDialog = {},
                    onConfirmPendingFabricMutationWithLayoutDeletion = {},
                    onOpenRenameFabricDialog = {},
                    onCloseRenameFabricDialog = {},
                    onFabricNameChanged = {},
                    onSaveRenamedFabric = {},
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithText("Project loading failed").assertIsDisplayed()
    }

    @Test
    fun shows_project_not_found_state() {
        composeRule.setContent {
            MaterialTheme {
                ProjectDetailScreen(
                    state = ProjectDetailUiState(project = null),
                    onViewPatternSuggestionsClick = {},
                    onGeneratedLayoutClick = { _, _ -> },
                    onDeleteProjectClick = {},
                    onOpenEditProjectDialog = {},
                    onCloseEditProjectDialog = {},
                    onEditedProjectNameChanged = {},
                    onEditedSeamAllowanceChanged ={},
                    onEditedRotationChanged = {},
                    onEditedAllowMirrorChanged = {},
                    onSaveEditedProject = {},
                    onCloseDeleteLayoutsAfterEditDialog = {},
                    onConfirmSaveEditedProjectWithLayoutDeletion = {},
                    onOpenFabricOptionsDialog = {},
                    onCloseFabricOptionsDialog = {},
                    onStartManualFabricAdd = {},
                    onStartImageFabricAdd = {},
                    onCloseManualFabricDialog = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onFabricWarpChanged = {},
                    onSaveManualFabricDraft = {},
                    onRequestDeleteFabric = {},
                    onCloseDeleteLayoutsAfterFabricChangeDialog = {},
                    onConfirmPendingFabricMutationWithLayoutDeletion = {},
                    onOpenRenameFabricDialog = {},
                    onCloseRenameFabricDialog = {},
                    onFabricNameChanged = {},
                    onSaveRenamedFabric = {},
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithText("Project not found").assertIsDisplayed()
    }

    @Test
    fun clicking_fabric_card_opens_shape_dialog() {
        val project = sampleProject(name = "Tailoring Project")

        composeRule.setContent {
            MaterialTheme {
                ProjectDetailScreen(
                    state = ProjectDetailUiState(project = project),
                    onViewPatternSuggestionsClick = {},
                    onGeneratedLayoutClick = { _, _ -> },
                    onDeleteProjectClick = {},
                    onOpenEditProjectDialog = {},
                    onCloseEditProjectDialog = {},
                    onEditedProjectNameChanged = {},
                    onEditedSeamAllowanceChanged ={},
                    onEditedRotationChanged = {},
                    onEditedAllowMirrorChanged = {},
                    onSaveEditedProject = {},
                    onCloseDeleteLayoutsAfterEditDialog = {},
                    onConfirmSaveEditedProjectWithLayoutDeletion = {},
                    onOpenFabricOptionsDialog = {},
                    onCloseFabricOptionsDialog = {},
                    onStartManualFabricAdd = {},
                    onStartImageFabricAdd = {},
                    onCloseManualFabricDialog = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onFabricWarpChanged = {},
                    onSaveManualFabricDraft = {},
                    onRequestDeleteFabric = {},
                    onCloseDeleteLayoutsAfterFabricChangeDialog = {},
                    onConfirmPendingFabricMutationWithLayoutDeletion = {},
                    onOpenRenameFabricDialog = {},
                    onCloseRenameFabricDialog = {},
                    onFabricNameChanged = {},
                    onSaveRenamedFabric = {},
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithText("Fabric 1").performClick()

        composeRule.onNodeWithText("Fabric 1 shape").assertIsDisplayed()
        composeRule.onNodeWithText("Close").assertIsDisplayed()
    }

    @Test
    fun clicking_choose_a_pattern_triggers_callback() {
        val project = sampleProject()
        var choosePatternClicked = false

        composeRule.setContent {
            MaterialTheme {
                ProjectDetailScreen(
                    state = ProjectDetailUiState(project = project),
                    onViewPatternSuggestionsClick = { choosePatternClicked = true },
                    onGeneratedLayoutClick = { _, _ -> },
                    onDeleteProjectClick = {},
                    onOpenEditProjectDialog = {},
                    onCloseEditProjectDialog = {},
                    onEditedProjectNameChanged = {},
                    onEditedSeamAllowanceChanged ={},
                    onEditedRotationChanged = {},
                    onEditedAllowMirrorChanged = {},
                    onSaveEditedProject = {},
                    onCloseDeleteLayoutsAfterEditDialog = {},
                    onConfirmSaveEditedProjectWithLayoutDeletion = {},
                    onOpenFabricOptionsDialog = {},
                    onCloseFabricOptionsDialog = {},
                    onStartManualFabricAdd = {},
                    onStartImageFabricAdd = {},
                    onCloseManualFabricDialog = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onFabricWarpChanged = {},
                    onSaveManualFabricDraft = {},
                    onRequestDeleteFabric = {},
                    onCloseDeleteLayoutsAfterFabricChangeDialog = {},
                    onConfirmPendingFabricMutationWithLayoutDeletion = {},
                    onOpenRenameFabricDialog = {},
                    onCloseRenameFabricDialog = {},
                    onFabricNameChanged = {},
                    onSaveRenamedFabric = {},
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithText("Choose a pattern").performClick()

        assertEquals(true, choosePatternClicked)
    }

    @Test
    fun clicking_delete_icon_opens_dialog() {
        val project = sampleProject()

        composeRule.setContent {
            MaterialTheme {
                ProjectDetailScreen(
                    state = ProjectDetailUiState(project = project),
                    onViewPatternSuggestionsClick = {},
                    onGeneratedLayoutClick = { _, _ -> },
                    onDeleteProjectClick = {},
                    onOpenEditProjectDialog = {},
                    onCloseEditProjectDialog = {},
                    onEditedProjectNameChanged = {},
                    onEditedSeamAllowanceChanged ={},
                    onEditedRotationChanged = {},
                    onEditedAllowMirrorChanged = {},
                    onSaveEditedProject = {},
                    onCloseDeleteLayoutsAfterEditDialog = {},
                    onConfirmSaveEditedProjectWithLayoutDeletion = {},
                    onOpenFabricOptionsDialog = {},
                    onCloseFabricOptionsDialog = {},
                    onStartManualFabricAdd = {},
                    onStartImageFabricAdd = {},
                    onCloseManualFabricDialog = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onFabricWarpChanged = {},
                    onSaveManualFabricDraft = {},
                    onRequestDeleteFabric = {},
                    onCloseDeleteLayoutsAfterFabricChangeDialog = {},
                    onConfirmPendingFabricMutationWithLayoutDeletion = {},
                    onOpenRenameFabricDialog = {},
                    onCloseRenameFabricDialog = {},
                    onFabricNameChanged = {},
                    onSaveRenamedFabric = {},
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithContentDescription("Delete project").performClick()

        composeRule.onNodeWithText("Delete project?").assertIsDisplayed()
        composeRule.onNodeWithText(
            "This will permanently delete the project and all generated layouts connected to it."
        ).assertIsDisplayed()
    }

    @Test
    fun confirming_delete_triggers_callback() {
        val project = sampleProject()
        var deleteClicked = false

        composeRule.setContent {
            MaterialTheme {
                ProjectDetailScreen(
                    state = ProjectDetailUiState(project = project),
                    onViewPatternSuggestionsClick = {},
                    onGeneratedLayoutClick = { _, _ -> },
                    onDeleteProjectClick = { deleteClicked = true },
                    onOpenEditProjectDialog = {},
                    onCloseEditProjectDialog = {},
                    onEditedProjectNameChanged = {},
                    onEditedSeamAllowanceChanged ={},
                    onEditedRotationChanged = {},
                    onEditedAllowMirrorChanged = {},
                    onSaveEditedProject = {},
                    onCloseDeleteLayoutsAfterEditDialog = {},
                    onConfirmSaveEditedProjectWithLayoutDeletion = {},
                    onOpenFabricOptionsDialog = {},
                    onCloseFabricOptionsDialog = {},
                    onStartManualFabricAdd = {},
                    onStartImageFabricAdd = {},
                    onCloseManualFabricDialog = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onFabricWarpChanged = {},
                    onSaveManualFabricDraft = {},
                    onRequestDeleteFabric = {},
                    onCloseDeleteLayoutsAfterFabricChangeDialog = {},
                    onConfirmPendingFabricMutationWithLayoutDeletion = {},
                    onOpenRenameFabricDialog = {},
                    onCloseRenameFabricDialog = {},
                    onFabricNameChanged = {},
                    onSaveRenamedFabric = {},
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithContentDescription("Delete project").performClick()

        composeRule
            .onAllNodesWithText("Delete")
            .filterToOne(hasClickAction())
            .performClick()

        assertEquals(true, deleteClicked)
    }

    @Test
    fun clicking_generated_layout_calls_callback_with_project_and_variant() {
        val project = sampleProject(id = "project-1", name = "Tailoring Project")
        var receivedProjectId: String? = null
        var receivedVariantId: String? = null

        composeRule.setContent {
            MaterialTheme {
                ProjectDetailScreen(
                    state = ProjectDetailUiState(
                        project = project,
                        generatedLayouts = listOf(
                            GeneratedLayoutCardUi(
                                nestingResultId = "nesting_result_1",
                                patternName = "Basic T-Shirt",
                                patternSize = "M",
                                variantId = "variant-m",
                            )
                        )
                    ),
                    onViewPatternSuggestionsClick = {},
                    onGeneratedLayoutClick = { projectId, variantId ->
                        receivedProjectId = projectId
                        receivedVariantId = variantId
                    },
                    onDeleteProjectClick = {},
                    onOpenEditProjectDialog = {},
                    onCloseEditProjectDialog = {},
                    onEditedProjectNameChanged = {},
                    onEditedSeamAllowanceChanged ={},
                    onEditedRotationChanged = {},
                    onEditedAllowMirrorChanged = {},
                    onSaveEditedProject = {},
                    onCloseDeleteLayoutsAfterEditDialog = {},
                    onConfirmSaveEditedProjectWithLayoutDeletion = {},
                    onOpenFabricOptionsDialog = {},
                    onCloseFabricOptionsDialog = {},
                    onStartManualFabricAdd = {},
                    onStartImageFabricAdd = {},
                    onCloseManualFabricDialog = {},
                    onFabricWidthChanged = {},
                    onFabricLengthChanged = {},
                    onFabricWarpChanged = {},
                    onSaveManualFabricDraft = {},
                    onRequestDeleteFabric = {},
                    onCloseDeleteLayoutsAfterFabricChangeDialog = {},
                    onConfirmPendingFabricMutationWithLayoutDeletion = {},
                    onOpenRenameFabricDialog = {},
                    onCloseRenameFabricDialog = {},
                    onFabricNameChanged = {},
                    onSaveRenamedFabric = {},
                    onBack = {},
                    onHome = {}
                )
            }
        }

        composeRule.onNodeWithText("Layout 1").performClick()

        assertEquals("project-1", receivedProjectId)
        assertEquals("variant-m", receivedVariantId)
    }
}