package cz.cvut.fit.bp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.cvut.fit.bp.navigation.AppRoutes
import cz.cvut.fit.bp.projectCreation.FabricImageScreen
import cz.cvut.fit.bp.projectCreation.ImagePickScreen
import cz.cvut.fit.bp.projectCreation.ProjectCreationScreen
import cz.cvut.fit.bp.projectCreation.ProjectCreationViewModel
import cz.cvut.fit.bp.ui.theme.BpTheme
import domain.geometry.setSeamAllowance
import runTestSolver

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        //TODO enable
        System.loadLibrary("opencv_java4")

        //testGeometry()

        setContent {
            BpTheme {
                //OpenCvTestScreen()
                //SolverPreviewScreen()
                AppNav()
            }
        }
    }

}

@Composable
fun SolverPreviewScreen() {
    val (problem, result) = runTestSolver()
    val piecesById = problem.pieces.associateBy { it.id }

    val seamAllowance = 10.0

    val piecesByIdSeamAllowance = problem.pieces
        .map { piece ->
            piece.copy(shape = piece.shape.setSeamAllowance(seamAllowance))
        }
        .associateBy { it.id }

    LayoutPreview(
        fabric = problem.project.fabrics.first().contour,
        piecesById = piecesById,
        piecesByIdSeamAllowance = piecesByIdSeamAllowance,
        placements = result.bestLayout.placed,
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    )
}

@Preview
@Composable
fun AppAndroidPreview() {
    SolverPreviewScreen()
}

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val viewModel: ProjectCreationViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.ProjectCreation
    ) {
        composable(AppRoutes.ProjectCreation) {
            val state = viewModel.uiState

            ProjectCreationScreen(
                state = state,
                onProjectNameChanged = viewModel::onProjectNameChanged,
                onSeamAllowanceChanged = viewModel::onSeamAllowanceChanged,
                onRotationChanged = viewModel::onRotationChanged,
                onAllowMirrorChanged = viewModel::onAllowMirrorChanged,
                onFabricModeChanged = viewModel::onFabricModeChanged,
                onFabricWidthChanged = viewModel::onFabricWidthChanged,
                onWarpChanged = viewModel::onWarpChanged,
                onFabricLengthChanged = viewModel::onFabricLengthChanged,
                onAddFabricClicked = viewModel::addCurrentFabric,
                onNextClicked = {
                    val project = viewModel.buildProject()
                    if (project != null) {
                        println(project)
                    }
                },
                onManualDialogDismiss = viewModel::closeManualDialog,
                onManualDialogConfirm = viewModel::confirmManualDialog,
                onOpenManualDialogue = viewModel::openManualDialog,
                onOpenImagePick = {
                    viewModel.startImageMode()
                    navController.navigate(AppRoutes.ImagePick)
                }
            )
        }

        composable(AppRoutes.ImagePick) {
            ImagePickScreen(
                onBack = { navController.popBackStack() },
                onPickTestImage = { imageName ->
                    viewModel.setResourceImage(imageName)
                    navController.navigate(AppRoutes.FabricImage)
                }
            )
        }

        composable(AppRoutes.FabricImage) {
            val state = viewModel.uiState

            FabricImageScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onConfirmContour = { contour ->
                    viewModel.confirmEditedContour(contour)
                },
                onConfirmA4 = { marker ->
                    viewModel.confirmEditedA4(marker)
                },
                onConfirmWarpTilt = { tiltDegrees ->
                    viewModel.confirmWarpTilt(tiltDegrees)
                },
                onFinishImageFabric = {
                    viewModel.confirmImageFabric()
                    navController.popBackStack(AppRoutes.ProjectCreation, false)
                }
            )
        }


    }
}