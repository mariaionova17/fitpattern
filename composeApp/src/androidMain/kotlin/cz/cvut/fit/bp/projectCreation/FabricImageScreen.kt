package cz.cvut.fit.bp.projectCreation

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cz.cvut.fit.bp.R
import cz.cvut.fit.bp.imageRecognition.EditableA4Overlay
import cz.cvut.fit.bp.imageRecognition.EditablePolygonOverlay
import cz.cvut.fit.bp.imageRecognition.FinalPolygonPreview
import cz.cvut.fit.bp.imageRecognition.WarpDirectionOverlay
import cz.cvut.fit.bp.imageRecognition.loadBitmapFromAssets
import cz.cvut.fit.bp.imageRecognition.markerDetection.Marker
import cz.cvut.fit.bp.imageRecognition.markerDetection.alignPolygonWarpToVertical
import cz.cvut.fit.bp.imageRecognition.markerDetection.detectA4
import cz.cvut.fit.bp.imageRecognition.markerDetection.editablePolygonToMmPolygon
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import cz.cvut.fit.bp.imageRecognition.polygonDetection.detectBestPolygon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FabricImageScreen(
    state: ProjectCreationUiState,
    onBack: () -> Unit,
    onConfirmContour: (List<CvPoint>) -> Unit,
    onConfirmA4: (Marker) -> Unit,
    onConfirmWarpTilt: (Double) -> Unit,
    onFinishImageFabric: () -> Unit
) {
    val context = LocalContext.current

    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var editablePolygon by remember { mutableStateOf<List<CvPoint>>(emptyList()) }
    var editableMarker by remember { mutableStateOf<Marker?>(null) }
    var warpTiltDegrees by remember { mutableStateOf(0.0) }
    var statusText by remember { mutableStateOf("No image selected") }
    var isProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(state.currentFabric.resourceImageName, state.currentImageStep) {
        val imageName = state.currentFabric.resourceImageName ?: return@LaunchedEffect

        try {
            isProcessing = true
            statusText = "Loading image..."

            val original = withContext(Dispatchers.Default) {
                loadBitmapFromAssets(context, imageName)
            }

            originalBitmap = original

            when (state.currentImageStep) {
                FabricImageStep.EDIT_CONTOUR -> {
                    val result = withContext(Dispatchers.Default) {
                        detectBestPolygon(original)
                    }

                    editablePolygon = state.currentFabric.editedContour?.map {
                        CvPoint(it.x, it.y)
                    } ?: result.bestCandidate?.polygon.orEmpty()

                    statusText = if (editablePolygon.isNotEmpty()) {
                        "Contour detected. You can edit it now."
                    } else {
                        "No contour detected."
                    }
                }

                FabricImageStep.EDIT_A4 -> {
                    editableMarker = state.currentFabric.editedA4Marker ?: detectA4(original)
                    statusText = "Adjust the A4 paper corners."
                }

                FabricImageStep.EDIT_WARP -> {
                    editablePolygon = state.currentFabric.editedContour?.map {
                        CvPoint(it.x, it.y)
                    }.orEmpty()

                    warpTiltDegrees = state.currentFabric.warpAngleDegreesFromVertical

                    statusText = "Rotate the arrow to define warp direction."
                }

                FabricImageStep.NONE -> Unit
            }
        } catch (e: Throwable) {
            statusText = "Error: ${e.message}"
        } finally {
            isProcessing = false
        }
    }
    val previewPolygon = remember(editablePolygon, editableMarker, warpTiltDegrees) {
        val marker = editableMarker
        if (editablePolygon.isEmpty() || marker == null) {
            null
        } else {
            val mmPolygon = editablePolygonToMmPolygon(editablePolygon, marker)
            alignPolygonWarpToVertical(mmPolygon, warpTiltDegrees)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = when (state.currentImageStep) {
                                FabricImageStep.EDIT_CONTOUR -> "Drag points to edit fabric contour"
                                FabricImageStep.EDIT_A4 -> "Drag points to edit A4 contour"
                                FabricImageStep.EDIT_WARP -> "Position arrow parallel to warp"
                                FabricImageStep.NONE -> "Image fabric"
                            },
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(id = R.drawable.back),
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 1.dp
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
                //.padding(horizontal = 40.dp, vertical = 24.dp)
                //.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /*Text(
                text = when (state.currentImageStep) {
                    FabricImageStep.EDIT_CONTOUR -> "Adjust the detected fabric contour."
                    FabricImageStep.EDIT_A4 -> "Adjust the A4 paper corners for proper scaling."
                    FabricImageStep.EDIT_WARP -> "Rotate the arrow so warp is defined."
                    FabricImageStep.NONE -> statusText
                },
                color = MaterialTheme.colorScheme.onBackground
            )*/

            val bitmap = originalBitmap
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (bitmap != null) {
                    when (state.currentImageStep) {
                        FabricImageStep.EDIT_CONTOUR -> {
                            if (editablePolygon.isNotEmpty()) {
                                EditablePolygonOverlay(
                                    bitmap = bitmap,
                                    polygon = editablePolygon,
                                    onPolygonChange = { editablePolygon = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        FabricImageStep.EDIT_A4 -> {
                            val marker = editableMarker
                            if (marker != null) {
                                EditableA4Overlay(
                                    bitmap = bitmap,
                                    marker = marker,
                                    onMarkerChange = { editableMarker = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        FabricImageStep.EDIT_WARP -> {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                WarpDirectionOverlay(
                                    bitmap = bitmap,
                                    polygon = editablePolygon,
                                    tiltDegrees = warpTiltDegrees,
                                    onTiltChanged = { warpTiltDegrees = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )

                                previewPolygon?.let {
                                    Text(
                                        text = "Final normalized preview",
                                        color = MaterialTheme.colorScheme.onBackground
                                    )

                                    FinalPolygonPreview(
                                        polygon = it,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                    )
                                }
                            }
                        }

                        FabricImageStep.NONE -> Unit
                    }
                }
            }

            Button(
                onClick = {
                    when (state.currentImageStep) {
                        FabricImageStep.EDIT_CONTOUR -> {
                            onConfirmContour(editablePolygon)
                        }

                        FabricImageStep.EDIT_A4 -> {
                            editableMarker?.let { onConfirmA4(it) }
                        }

                        FabricImageStep.EDIT_WARP -> {
                            onConfirmWarpTilt(warpTiltDegrees)
                            onFinishImageFabric()
                        }

                        FabricImageStep.NONE -> Unit
                    }
                },
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp)
            ) {
                Text(
                    when (state.currentImageStep) {
                        FabricImageStep.EDIT_CONTOUR -> "Confirm contour"
                        FabricImageStep.EDIT_A4 -> "Confirm A4"
                        FabricImageStep.EDIT_WARP -> "Save fabric"
                        FabricImageStep.NONE -> "Confirm"
                    }
                )
            }
        }
    }
}