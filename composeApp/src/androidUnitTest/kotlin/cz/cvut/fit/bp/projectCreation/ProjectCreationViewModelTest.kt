package cz.cvut.fit.bp.projectCreation

import cz.cvut.fit.bp.FakeProjectRepository
import cz.cvut.fit.bp.imageRecognition.markerDetection.Marker
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import cz.cvut.fit.bp.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProjectCreationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `simple field setters update ui state`() {
        val viewModel = ProjectCreationViewModel(FakeProjectRepository())

        viewModel.onProjectNameChanged("My project")
        viewModel.onSeamAllowanceChanged("12")
        viewModel.onRotationChanged(UiRotationOption.BY180)
        viewModel.onAllowMirrorChanged(true)
        viewModel.onFabricModeChanged(FabricInputMode.MANUAL)
        viewModel.onFabricWidthChanged("150")
        viewModel.onFabricLengthChanged("200")
        viewModel.onWarpChanged(true)

        assertEquals("My project", viewModel.uiState.projectName)
        assertEquals("12", viewModel.uiState.seamAllowance)
        assertEquals(UiRotationOption.BY180, viewModel.uiState.rotationOption)
        assertEquals(true, viewModel.uiState.allowMirror)
        assertEquals(FabricInputMode.MANUAL, viewModel.uiState.currentFabric.inputMode)
        assertEquals("150", viewModel.uiState.currentFabric.widthMm)
        assertEquals("200", viewModel.uiState.currentFabric.lengthMm)
        assertEquals(true, viewModel.uiState.currentFabric.warpAlongWidth)
    }

    @Test
    fun `openManualDialog opens dialog and switches current fabric to manual mode`() {
        val viewModel = ProjectCreationViewModel(FakeProjectRepository())

        viewModel.openManualDialog()

        assertEquals(true, viewModel.uiState.isManualDialogOpen)
        assertEquals(FabricInputMode.MANUAL, viewModel.uiState.currentFabric.inputMode)
    }

    @Test
    fun `closeManualDialog closes dialog`() {
        val viewModel = ProjectCreationViewModel(FakeProjectRepository())

        viewModel.openManualDialog()
        viewModel.closeManualDialog()

        assertEquals(false, viewModel.uiState.isManualDialogOpen)
    }

    @Test
    fun `startImageMode initializes image mode and clears root error`() {
        val viewModel = ProjectCreationViewModel(FakeProjectRepository())

        viewModel.startImageMode()

        assertEquals(FabricInputMode.IMAGE, viewModel.uiState.currentFabric.inputMode)
        assertNull(viewModel.uiState.errorMessage)
    }

    @Test
    fun `setImageUri updates draft and moves to contour editing step`() {
        val viewModel = ProjectCreationViewModel(FakeProjectRepository())

        viewModel.setImageUri("content://image/1")

        assertEquals(FabricInputMode.IMAGE, viewModel.uiState.currentFabric.inputMode)
        assertEquals("content://image/1", viewModel.uiState.currentFabric.imageUri)
        assertEquals(null, viewModel.uiState.currentFabric.resourceImageName)
        assertEquals(FabricImageStep.EDIT_CONTOUR, viewModel.uiState.currentImageStep)
        assertNull(viewModel.uiState.errorMessage)
    }

    @Test
    fun `setResourceImage updates draft and moves to contour editing step`() {
        val viewModel = ProjectCreationViewModel(FakeProjectRepository())

        viewModel.setResourceImage("fabric_photo")

        assertEquals(FabricInputMode.IMAGE, viewModel.uiState.currentFabric.inputMode)
        assertEquals("fabric_photo", viewModel.uiState.currentFabric.resourceImageName)
        assertEquals(null, viewModel.uiState.currentFabric.imageUri)
        assertEquals(FabricImageStep.EDIT_CONTOUR, viewModel.uiState.currentImageStep)
        assertNull(viewModel.uiState.errorMessage)
    }

    @Test
    fun `confirmEditedContour stores contour and moves to a4 editing`() {
        val viewModel = ProjectCreationViewModel(FakeProjectRepository())
        val contour = listOf(
            CvPoint(0.0, 0.0),
            CvPoint(10.0, 0.0),
            CvPoint(10.0, 10.0),
            CvPoint(0.0, 10.0)
        )

        viewModel.confirmEditedContour(contour)

        assertEquals(contour, viewModel.uiState.currentFabric.editedContour)
        assertEquals(FabricImageStep.EDIT_A4, viewModel.uiState.currentImageStep)
        assertNull(viewModel.uiState.errorMessage)
    }

    @Test
    fun `confirmEditedA4 stores marker and moves to warp editing`() {
        val viewModel = ProjectCreationViewModel(FakeProjectRepository())
        val marker = Marker(
            topLeft = CvPoint(0.0, 0.0),
            topRight = CvPoint(10.0, 0.0),
            bottomRight = CvPoint(10.0, 20.0),
            bottomLeft = CvPoint(0.0, 20.0)
        )

        viewModel.confirmEditedA4(marker)

        assertEquals(marker, viewModel.uiState.currentFabric.editedA4Marker)
        assertEquals(FabricImageStep.EDIT_WARP, viewModel.uiState.currentImageStep)
        assertNull(viewModel.uiState.errorMessage)
    }

    @Test
    fun `confirmWarpTilt stores selected tilt angle`() {
        val viewModel = ProjectCreationViewModel(FakeProjectRepository())

        viewModel.confirmWarpTilt(37.5)

        assertEquals(37.5, viewModel.uiState.currentFabric.warpAngleDegreesFromVertical)
        assertNull(viewModel.uiState.errorMessage)
    }

    @Test
    fun `clearImageDraft resets current fabric and clears error`() {
        val viewModel = ProjectCreationViewModel(FakeProjectRepository())

        viewModel.setImageUri("content://image/1")
        viewModel.clearImageDraft()

        assertEquals(FabricDraft(), viewModel.uiState.currentFabric)
        assertNull(viewModel.uiState.errorMessage)
    }

    @Test
    fun `buildProject returns null and stores error when name is blank`() {
        val viewModel = ProjectCreationViewModel(FakeProjectRepository())

        val project = viewModel.buildProject()

        assertNull(project)
        assertEquals("Project name is required", viewModel.uiState.errorMessage)
    }

    @Test
    fun `saveProject does not call repository when buildProject fails`() = runTest {
        val repository = FakeProjectRepository()
        val viewModel = ProjectCreationViewModel(repository)

        var savedId: String? = null
        viewModel.saveProject { savedId = it }
        advanceUntilIdle()

        assertTrue(repository.savedProjects.isEmpty())
        assertNull(savedId)
        assertEquals("Project name is required", viewModel.uiState.errorMessage)
    }
}
