package cz.cvut.fit.bp.navigation

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.cvut.fit.bp.layoutPage.LayoutScreen
import cz.cvut.fit.bp.layoutPage.LayoutUiState
import cz.cvut.fit.bp.patternDetailPage.PatternDetailScreen
import cz.cvut.fit.bp.patternDetailPage.PatternDetailUiState
import cz.cvut.fit.bp.patternSuggestionsPage.PatternSuggestionsScreen
import cz.cvut.fit.bp.patternSuggestionsPage.PatternSuggestionsUiState
import cz.cvut.fit.bp.projectCreation.FabricDraft
import cz.cvut.fit.bp.projectCreation.FabricImageScreen
import cz.cvut.fit.bp.projectCreation.FabricImageStep
import cz.cvut.fit.bp.projectCreation.ProjectCreationScreen
import cz.cvut.fit.bp.projectCreation.ProjectCreationUiState
import cz.cvut.fit.bp.projectCreation.SelectImageScreen
import cz.cvut.fit.bp.projectCreation.UiRotationOption
import cz.cvut.fit.bp.projectDetailPage.GeneratedLayoutCardUi
import cz.cvut.fit.bp.projectDetailPage.ProjectDetailScreen
import cz.cvut.fit.bp.projectDetailPage.ProjectDetailUiState
import cz.cvut.fit.bp.projectsPage.ProjectsScreen
import cz.cvut.fit.bp.projectsPage.ProjectsUiState
import domain.fabric.FabricPiece
import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.pattern.Pattern
import domain.pattern.PatternPiece
import domain.pattern.PatternVariant
import domain.pattern.PlacementRules
import domain.pattern.RotationRules
import domain.project.Project
import org.junit.Rule
import org.junit.Test

class NavigationFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun projects_to_project_creation_navigation_works() {
        composeRule.setContent {
            MaterialTheme {
                TestNavHost(startDestination = "projects")
            }
        }

        composeRule.onNodeWithText("New project", useUnmergedTree = true).performClick()

        composeRule.waitForIdle()

        composeRule.onNodeWithText("Name:").assertIsDisplayed()
        composeRule.onNodeWithText("Seam allowance (mm):").assertIsDisplayed()
        composeRule.onNodeWithText("Are pattern rotations allowed?").assertIsDisplayed()
    }

    @Test
    fun projects_to_project_detail_navigation_works() {
        composeRule.setContent {
            MaterialTheme {
                TestNavHost(startDestination = "projects")
            }
        }

        composeRule.onNodeWithText("Nav Project").performClick()

        composeRule.onNodeWithText("Project information").assertIsDisplayed()
        composeRule.onNodeWithText("Choose a pattern").assertIsDisplayed()
    }

    @Test
    fun project_creation_back_returns_to_projects() {
        composeRule.setContent {
            MaterialTheme {
                TestNavHost(startDestination = "projects")
            }
        }

        composeRule
            .onNodeWithContentDescription("Create project", useUnmergedTree = true)
            .performClick()

        composeRule.onNodeWithText("Name:").assertIsDisplayed()
        composeRule.onNodeWithText("Are pattern rotations allowed?").assertIsDisplayed()

        composeRule
            .onNodeWithContentDescription("Back", useUnmergedTree = true)
            .performClick()

        composeRule.onNodeWithText("Projects").assertIsDisplayed()
        composeRule.onNodeWithText("Nav Project").assertIsDisplayed()
    }

    @Test
    fun project_creation_to_select_image_navigation_works() {
        composeRule.setContent {
            MaterialTheme {
                TestNavHost(startDestination = "project_creation")
            }
        }

        composeRule
            .onAllNodesWithText("Add an image")
            .filterToOne(hasClickAction())
            .performClick()

        composeRule.onNodeWithText("Add an image").assertIsDisplayed()
        composeRule.onNodeWithText("Pick image from gallery").assertIsDisplayed()
    }

    @Test
    fun select_image_back_returns_to_project_creation() {
        composeRule.setContent {
            MaterialTheme {
                TestNavHost(startDestination = "project_creation")
            }
        }

        composeRule.onNodeWithText("Add an image").performClick()
        composeRule.onNodeWithText("Pick image from gallery").assertIsDisplayed()

        composeRule
            .onNodeWithContentDescription("Back", useUnmergedTree = true)
            .performClick()

        composeRule.waitForIdle()

        composeRule.onNodeWithText("Name:").assertIsDisplayed()
        composeRule.onNodeWithText("Are pattern rotations allowed?").assertIsDisplayed()
        composeRule.onNodeWithText("Seam allowance (mm):").assertIsDisplayed()
    }

    @Test
    fun select_image_test_button_navigates_to_fabric_image_screen() {
        composeRule.setContent {
            MaterialTheme {
                TestNavHost(startDestination = "select_image")
            }
        }

        composeRule
            .onAllNodesWithText("Use test_image.jpg")
            .filterToOne(hasClickAction())
            .performClick()

        composeRule.onNodeWithText("Image fabric").assertIsDisplayed()
        composeRule.onNodeWithText("Confirm").assertIsDisplayed()
    }

    @Test
    fun project_detail_to_pattern_suggestions_navigation_works() {
        composeRule.setContent {
            MaterialTheme {
                TestNavHost(startDestination = "project_detail")
            }
        }

        composeRule.onNodeWithText("Choose a pattern").performClick()

        composeRule.onNodeWithText("Pattern suggestions").assertIsDisplayed()
        composeRule.onNodeWithText("Pattern Alpha").assertIsDisplayed()
    }

    @Test
    fun pattern_suggestions_back_returns_to_project_detail() {
        composeRule.setContent {
            MaterialTheme {
                TestNavHost(startDestination = "pattern_suggestions")
            }
        }

        composeRule.onNodeWithContentDescription("Back").performClick()

        composeRule.onNodeWithText("Project information").assertIsDisplayed()
    }

    @Test
    fun pattern_suggestions_to_pattern_detail_navigation_works() {
        composeRule.setContent {
            MaterialTheme {
                TestNavHost(startDestination = "pattern_suggestions")
            }
        }

        composeRule.onNodeWithText("Pattern Alpha").performClick()

        composeRule.onNodeWithText("Choose the size").assertIsDisplayed()
        composeRule.onNodeWithText("Open layout").assertIsDisplayed()
    }

    @Test
    fun pattern_detail_back_returns_to_pattern_suggestions() {
        composeRule.setContent {
            MaterialTheme {
                TestNavHost(startDestination = "pattern_detail")
            }
        }

        composeRule.onNodeWithContentDescription("Back").performClick()

        composeRule.onNodeWithText("Pattern suggestions").assertIsDisplayed()
    }

    @Test
    fun pattern_detail_to_layout_navigation_works() {
        composeRule.setContent {
            MaterialTheme {
                TestNavHost(startDestination = "pattern_detail")
            }
        }

        composeRule.onNodeWithText("Open layout").performClick()

        composeRule.onNodeWithText("Generated layout").assertIsDisplayed()
        composeRule.onNodeWithText("No layout data").assertIsDisplayed()
    }

    @Test
    fun project_detail_generated_layout_click_navigates_to_layout_screen() {
        composeRule.setContent {
            MaterialTheme {
                TestNavHost(startDestination = "project_detail")
            }
        }

        composeRule.onNodeWithText("Layout 1").performClick()

        composeRule.onNodeWithText("Generated layout").assertIsDisplayed()
        composeRule.onNodeWithText("No layout data").assertIsDisplayed()
    }

    @Test
    fun layout_back_returns_to_previous_screen() {
        composeRule.setContent {
            MaterialTheme {
                TestNavHost(startDestination = "pattern_detail")
            }
        }

        composeRule.onNodeWithText("Open layout").performClick()
        composeRule.onNodeWithContentDescription("Back").performClick()

        composeRule.onNodeWithText("Choose the size").assertIsDisplayed()
        composeRule.onNodeWithText("Open layout").assertIsDisplayed()
    }
}

@Composable
private fun TestNavHost(startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("projects") {
            ProjectsScreen(
                state = ProjectsUiState(
                    projects = listOf(sampleProject(name = "Nav Project"))
                ),
                onCreateProjectClick = { navController.navigate("project_creation") },
                onProjectClick = { navController.navigate("project_detail") }
            )
        }

        composable("project_creation") {
            ProjectCreationScreen(
                state = ProjectCreationUiState(
                    rotationOption = UiRotationOption.NONE
                ),
                onProjectNameChanged = {},
                onSeamAllowanceChanged = {},
                onRotationChanged = {},
                onAllowMirrorChanged = {},
                onFabricWidthChanged = {},
                onFabricLengthChanged = {},
                onWarpChanged = {},
                onNextClicked = { navController.navigate("project_detail") },
                onManualDialogDismiss = {},
                onManualDialogConfirm = {},
                onOpenManualDialogue = {},
                onOpenSelectImage = { navController.navigate("select_image") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("select_image") {
            SelectImageScreen(
                onBack = { navController.popBackStack() },
                onPickTestImage = { navController.navigate("fabric_image") },
                onPickImageFromGallery = { navController.navigate("fabric_image") }
            )
        }

        composable("fabric_image") {
            FabricImageScreen(
                currentFabric = FabricDraft(),
                currentImageStep = FabricImageStep.NONE,
                onBack = { navController.popBackStack() },
                onConfirmContour = {},
                onConfirmA4 = {},
                onConfirmWarpTilt = {},
                onFinishImageFabric = {}
            )
        }

        composable("project_detail") {
            ProjectDetailScreen(
                state = ProjectDetailUiState(
                    project = sampleProject(name = "Nav Project"),
                    generatedLayouts = listOf(
                        GeneratedLayoutCardUi(
                            nestingResultId = "nest-1",
                            patternName = "Pattern Alpha",
                            patternSize = "M",
                            variantId = "variant-m"
                        )
                    )
                ),
                onViewPatternSuggestionsClick = { navController.navigate("pattern_suggestions") },
                onGeneratedLayoutClick = { _, _ -> navController.navigate("layout") },
                onDeleteProjectClick = {},
                onOpenEditProjectDialog = {},
                onCloseEditProjectDialog = {},
                onEditedProjectNameChanged = {},
                onEditedSeamAllowanceChanged = {},
                onEditedRotationChanged = {},
                onEditedAllowMirrorChanged = {},
                onSaveEditedProject = {},
                onCloseDeleteLayoutsAfterEditDialog = {},
                onConfirmSaveEditedProjectWithLayoutDeletion = {},

                onOpenFabricOptionsDialog = {},
                onCloseFabricOptionsDialog = {},
                onStartManualFabricAdd = {},
                onStartImageFabricAdd = { navController.navigate("select_image") },
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

                onBack = { navController.popBackStack() },
                onHome = {}
            )
        }

        composable("pattern_suggestions") {
            PatternSuggestionsScreen(
                state = PatternSuggestionsUiState(
                    patterns = listOf(samplePattern(name = "Pattern Alpha"))
                ),
                onPatternClick = { navController.navigate("pattern_detail") },
                onBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate("project_detail")
                    }
                },
                onHome = {}
            )
        }

        composable("pattern_detail") {
            PatternDetailScreen(
                state = PatternDetailUiState(
                    pattern = samplePattern(name = "Pattern Alpha")
                ),
                onBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate("pattern_suggestions")
                    }
                },
                onHome = {},
                onViewLayoutClick = { navController.navigate("layout") }
            )
        }

        composable("layout") {
            LayoutScreen(
                state = LayoutUiState(),
                onBack = { navController.popBackStack() },
                onHome = {}
            )
        }
    }
}

private fun sampleProject(name: String): Project {
    return Project(
        id = "project-1",
        name = name,
        fabricPieces = listOf(
            FabricPiece(
                id = "fabric-1",
                name = "Fabric 1",
                contour = Shape(
                    contour = Polygon(
                        points = listOf(
                            Point(0.0, 0.0),
                            Point(300.0, 0.0),
                            Point(300.0, 500.0),
                            Point(0.0, 500.0)
                        )
                    ),
                    holes = emptyList()
                )
            )
        ),
        rules = PlacementRules(
            rotation = RotationRules(listOf(0.0)),
            allowMirror = false
        ),
        seamAllowance = 10.0
    )
}

private fun samplePattern(name: String): Pattern {
    return Pattern(
        id = "pattern-1",
        name = name,
        pictureUri = "",
        downloadLink = "",
        variants = listOf(
            PatternVariant(
                id = "variant-m",
                size = "M",
                pieces = listOf(
                    PatternPiece(
                        id = "piece-1",
                        name = "Front piece",
                        shape = Shape(
                            contour = Polygon(
                                points = listOf(
                                    Point(0.0, 0.0),
                                    Point(100.0, 0.0),
                                    Point(100.0, 120.0),
                                    Point(0.0, 120.0)
                                )
                            ),
                            holes = emptyList()
                        )
                    )
                )
            )
        )
    )
}