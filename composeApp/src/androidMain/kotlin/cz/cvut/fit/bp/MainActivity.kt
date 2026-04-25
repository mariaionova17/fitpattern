package cz.cvut.fit.bp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.cvut.fit.bp.layoutPage.LayoutScreen
import cz.cvut.fit.bp.layoutPage.LayoutViewModel
import cz.cvut.fit.bp.layoutPage.LayoutViewModelFactory
import cz.cvut.fit.bp.navigation.AppRoutes
import cz.cvut.fit.bp.patternDetailPage.PatternDetailScreen
import cz.cvut.fit.bp.patternDetailPage.PatternDetailViewModel
import cz.cvut.fit.bp.patternDetailPage.PatternDetailViewModelFactory
import cz.cvut.fit.bp.patternSuggestionsPage.PatternSuggestionsScreen
import cz.cvut.fit.bp.patternSuggestionsPage.PatternSuggestionsViewModel
import cz.cvut.fit.bp.patternSuggestionsPage.PatternSuggestionsViewModelFactory
import cz.cvut.fit.bp.projectCreation.FabricImageScreen
import cz.cvut.fit.bp.projectCreation.SelectImageScreen
import cz.cvut.fit.bp.projectCreation.ProjectCreationScreen
import cz.cvut.fit.bp.projectCreation.ProjectCreationViewModel
import cz.cvut.fit.bp.projectCreation.ProjectCreationViewModelFactory
import cz.cvut.fit.bp.shareddb.data.db.createDatabase
import cz.cvut.fit.bp.shareddb.data.repository.ProjectRepositoryImpl
import cz.cvut.fit.bp.ui.theme.BpTheme
import domain.pattern.PlacementRules
import domain.pattern.RotationRules
import domain.project.Project
import domain.repository.PatternRepository
import domain.repository.ProjectRepository
import kotlinx.coroutines.launch
import cz.cvut.fit.bp.projectDetailPage.ProjectDetailScreen
import cz.cvut.fit.bp.projectDetailPage.ProjectDetailViewModel
import cz.cvut.fit.bp.projectDetailPage.ProjectDetailViewModelFactory
import cz.cvut.fit.bp.projectsPage.ProjectsViewModel
import cz.cvut.fit.bp.projectsPage.ProjectsViewModelFactory
import cz.cvut.fit.bp.projectsPage.ProjectsScreen
import cz.cvut.fit.bp.shareddb.data.PatternSeeder
import domain.repository.NestingResultRepository
import domain.solver.BetterSolver

class MainActivity : ComponentActivity() {
    private val appContainer by lazy {
        (application as MyApplication).appContainer
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        System.loadLibrary("opencv_java4")

        //testDatabase()
        //testGeometry()
        seedPatternsIfNeeded()

        setContent {
            BpTheme {
                //OpenCvTestScreen()
                //SolverPreviewScreen()
                AppNav(
                    projectRepository = appContainer.projectRepository,
                    patternRepository = appContainer.patternRepository,
                    nestingResultRepository = appContainer.nestingResultRepository,
                    solver = appContainer.nestingSolver
                )
            }
        }
    }

    private fun seedPatternsIfNeeded() {
        lifecycleScope.launch {
            val dao = createDatabase(this@MainActivity).patternDao()

            if (dao.getAllPatterns().isEmpty()) {
                PatternSeeder.insertSamplePatterns(dao)
            }
        }
    }
    private fun testDatabase() {
        val db = createDatabase(this)
        val projectRepository = ProjectRepositoryImpl(db)

        lifecycleScope.launch {
            val project = Project(
                id = "project_1",
                name = "Test project",
                fabricPieces = emptyList(),
                rules = PlacementRules(
                    rotation = RotationRules(listOf(0.0, 90.0)),
                    allowMirror = true
                ),
                seamAllowance = 1.5
            )

            projectRepository.saveProject(project)

            val loadedProject = projectRepository.getProjectById("project_1")

            println("Loaded project: $loadedProject")
        }
    }

}

/*@Composable
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
        fabric = problem.project.fabricPieces.first().contour,
        piecesById = piecesById,
        piecesByIdSeamAllowance = piecesByIdSeamAllowance,
        placements = result.bestLayout.placed,
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    )
}*

@Preview
@Composable
fun AppAndroidPreview() {
    SolverPreviewScreen()
}*/

@Composable
fun AppNav(
    projectRepository: ProjectRepository,
    patternRepository: PatternRepository,
    nestingResultRepository: NestingResultRepository,
    solver: BetterSolver
) {
    val navController = rememberNavController()
    val projectCreationViewModel: ProjectCreationViewModel = viewModel(factory = ProjectCreationViewModelFactory(projectRepository))
    val projectsViewModel: ProjectsViewModel = viewModel(factory = ProjectsViewModelFactory(projectRepository))
    val patternSuggestionsViewModel: PatternSuggestionsViewModel = viewModel(factory = PatternSuggestionsViewModelFactory(patternRepository))

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Projects
    ) {

        composable(AppRoutes.Projects) {
            androidx.compose.runtime.LaunchedEffect(Unit) {
                projectsViewModel.loadProjects()
            }

            ProjectsScreen(
                state = projectsViewModel.uiState,
                onCreateProjectClick = {
                    navController.navigate(AppRoutes.ProjectCreation)
                },
                onProjectClick = { projectId ->
                    navController.navigate(AppRoutes.projectDetail(projectId))
                }
            )
        }

        composable(AppRoutes.ProjectCreation) {
            val state = projectCreationViewModel.uiState

            ProjectCreationScreen(
                state = state,
                onProjectNameChanged = projectCreationViewModel::onProjectNameChanged,
                onSeamAllowanceChanged = projectCreationViewModel::onSeamAllowanceChanged,
                onRotationChanged = projectCreationViewModel::onRotationChanged,
                onAllowMirrorChanged = projectCreationViewModel::onAllowMirrorChanged,
                onFabricWidthChanged = projectCreationViewModel::onFabricWidthChanged,
                onWarpChanged = projectCreationViewModel::onWarpChanged,
                onFabricLengthChanged = projectCreationViewModel::onFabricLengthChanged,
                onNextClicked = {
                    projectCreationViewModel.saveProject { projectId ->
                        navController.navigate(AppRoutes.projectDetail(projectId))
                    }
                },
                onManualDialogDismiss = projectCreationViewModel::closeManualDialog,
                onManualDialogConfirm = projectCreationViewModel::confirmManualDialog,
                onOpenManualDialogue = projectCreationViewModel::openManualDialog,
                onOpenSelectImage = {
                    projectCreationViewModel.startImageMode()
                    navController.navigate(AppRoutes.SelectImage)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.SelectImage) {
            SelectImageScreen(
                onBack = { navController.popBackStack() },
                onPickTestImage = { imageName ->
                    projectCreationViewModel.setResourceImage(imageName)
                    navController.navigate(AppRoutes.FabricImage)
                },
                onPickImageFromGallery = { imageUri ->
                    projectCreationViewModel.setImageUri(imageUri)
                    navController.navigate(AppRoutes.FabricImage)
                }
            )
        }

        composable(AppRoutes.FabricImage) {
            val state = projectCreationViewModel.uiState

            FabricImageScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onConfirmContour = { contour ->
                    projectCreationViewModel.confirmEditedContour(contour)
                },
                onConfirmA4 = { marker ->
                    projectCreationViewModel.confirmEditedA4(marker)
                },
                onConfirmWarpTilt = { tiltDegrees ->
                    projectCreationViewModel.confirmWarpTilt(tiltDegrees)
                },
                onFinishImageFabric = {
                    projectCreationViewModel.confirmImageFabric()
                    navController.popBackStack(AppRoutes.ProjectCreation, false)
                }
            )
        }

        composable(AppRoutes.ProjectDetailRoute) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")

            val detailViewModel: ProjectDetailViewModel = viewModel(
                factory = ProjectDetailViewModelFactory(projectRepository, patternRepository, nestingResultRepository)
            )

            if (projectId != null) {
                androidx.compose.runtime.LaunchedEffect(projectId) {
                    detailViewModel.loadProject(projectId)
                }
            }

            ProjectDetailScreen(
                state = detailViewModel.uiState,
                onViewPatternSuggestionsClick = {
                    if (projectId != null) {
                        navController.navigate(AppRoutes.patternSuggestions(projectId))
                    }
                },
                onGeneratedLayoutClick = { projectId, variantId ->
                    navController.navigate(AppRoutes.layout(projectId, variantId))},
                onDeleteProjectClick = {
                    detailViewModel.deleteProject {
                        navController.popBackStack()
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.Projects) {
            androidx.compose.runtime.LaunchedEffect(Unit) {
                projectsViewModel.loadProjects()
            }

            ProjectsScreen(
                state = projectsViewModel.uiState,
                onCreateProjectClick = {
                    navController.navigate(AppRoutes.ProjectCreation)
                },
                onProjectClick = { projectId ->
                    navController.navigate(AppRoutes.projectDetail(projectId))
                }
            )
        }

        composable(AppRoutes.PatternSuggestionsRoute) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            androidx.compose.runtime.LaunchedEffect(Unit) {
                patternSuggestionsViewModel.loadPatterns()
            }

            PatternSuggestionsScreen(
                state = patternSuggestionsViewModel.uiState,
                onPatternClick = { patternId ->
                    navController.navigate(AppRoutes.patternDetail(projectId, patternId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.PatternDetailRoute) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val patternId = backStackEntry.arguments?.getString("patternId") ?: return@composable


            val detailViewModel: PatternDetailViewModel = viewModel(
                factory = PatternDetailViewModelFactory(patternRepository)
            )

            androidx.compose.runtime.LaunchedEffect(patternId) {
                detailViewModel.loadPattern(patternId)
            }

            PatternDetailScreen(
                state = detailViewModel.uiState,
                onBack = { navController.popBackStack() },
                onViewLayoutClick = { variantId ->
                    navController.navigate(AppRoutes.layout(projectId, variantId))
                }
            )
        }

        composable(AppRoutes.LayoutRoute) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val variantId = backStackEntry.arguments?.getString("variantId") ?: return@composable

            val layoutViewModel: LayoutViewModel = viewModel(
                factory = LayoutViewModelFactory(
                    projectRepository = projectRepository,
                    patternRepository = patternRepository,
                    nestingResultRepository = nestingResultRepository,
                    solver = solver
                )
            )

            androidx.compose.runtime.LaunchedEffect(projectId, variantId) {
                layoutViewModel.openOrGenerateLayout(projectId, variantId)
            }
            LayoutScreen(
                state = layoutViewModel.uiState,
                onBack = { navController.popBackStack() }
            )
        }

    }
}