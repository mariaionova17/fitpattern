package cz.cvut.fit.bp.projectCreation

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import cz.cvut.fit.bp.imageRecognition.EditablePolygonOverlay
import cz.cvut.fit.bp.imageRecognition.loadBitmapFromAssets
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import cz.cvut.fit.bp.imageRecognition.polygonDetection.detectBestPolygon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageFabricScreen(
    state: ProjectCreationUiState,
    onBack: () -> Unit,
    onConfirmContour: (List<CvPoint>) -> Unit
) {
    val context = LocalContext.current

    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var editablePolygon by remember { mutableStateOf<List<CvPoint>>(emptyList()) }
    var statusText by remember { mutableStateOf("No image selected") }
    var isProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(state.currentFabric.resourceImageName) {
        val imageName = state.currentFabric.resourceImageName ?: return@LaunchedEffect

        try {
            isProcessing = true
            statusText = "Loading image and detecting contour..."

            val original = withContext(Dispatchers.Default) {
                loadBitmapFromAssets(context, imageName)
            }

            val result = withContext(Dispatchers.Default) {
                detectBestPolygon(original)
            }

            originalBitmap = original
            editablePolygon = result.bestCandidate?.polygon ?: emptyList()

            statusText = if (editablePolygon.isNotEmpty()) {
                "Contour detected. You can edit it now."
            } else {
                "No contour detected."
            }
        } catch (e: Throwable) {
            statusText = "Error: ${e.message}"
        } finally {
            isProcessing = false
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Edit contour",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.headlineLarge
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
                .padding(innerPadding)
                .padding(horizontal = 40.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = statusText,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (originalBitmap != null && editablePolygon.isNotEmpty()) {
            EditablePolygonOverlay(
                bitmap = originalBitmap!!,
                polygon = editablePolygon,
                onPolygonChange = { updated ->
                    editablePolygon = updated
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                onConfirmContour(editablePolygon)
                onBack()
            },
            enabled = editablePolygon.isNotEmpty() && !isProcessing,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm contour")
        }
    }
}
}