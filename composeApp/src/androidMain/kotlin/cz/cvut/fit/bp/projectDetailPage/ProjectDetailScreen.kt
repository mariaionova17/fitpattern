package cz.cvut.fit.bp.projectDetailPage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cz.cvut.fit.bp.LayoutPreview
import cz.cvut.fit.bp.R
import cz.cvut.fit.bp.projectCreation.UiRotationOption
import domain.geometry.rectangleSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    state: ProjectDetailUiState,
    onViewPatternSuggestionsClick: () -> Unit,
    onGeneratedLayoutClick: (String, String) -> Unit,
    onDeleteProjectClick: () -> Unit,
    onOpenEditProjectDialog: () -> Unit,
    onCloseEditProjectDialog: () -> Unit,
    onEditedProjectNameChanged: (String) -> Unit,
    onEditedSeamAllowanceChanged: (String) -> Unit,
    onEditedRotationChanged: (UiRotationOption) -> Unit,
    onEditedAllowMirrorChanged: (Boolean) -> Unit,
    onSaveEditedProject: () -> Unit,
    onCloseDeleteLayoutsAfterEditDialog: () -> Unit,
    onConfirmSaveEditedProjectWithLayoutDeletion: () -> Unit,
    onBack: () -> Unit,
    onHome: () -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedFabricIndex by remember { mutableStateOf<Int?>(null) }
    var showDeleteProjectDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = state.project?.name ?: "Project detail",
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
                    actions = {
                        IconButton(onClick = { showDeleteProjectDialog = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.delete),
                                contentDescription = "Delete project",
                                tint = MaterialTheme.colorScheme.error
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
        },
        bottomBar = {
            Column {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 1.dp
                )

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    NavigationBarItem(
                        selected = false,
                        onClick = onHome,
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.home),
                                contentDescription = "Home",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading project...",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            state.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            state.project == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Project not found",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                val project = state.project

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(scrollState)
                        .padding(innerPadding)
                        .padding(horizontal = 40.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Project information",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        IconButton(onClick = onOpenEditProjectDialog) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = "Edit project",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DetailRow("Name", project.name)

                            val displaySeamAllowance =
                                if (project.seamAllowance % 1 == 0.0) {
                                    project.seamAllowance.toInt()
                                } else {
                                    project.seamAllowance
                                }

                            DetailRow(
                                "Seam allowance",
                                "$displaySeamAllowance mm"
                            )

                            DetailRow(
                                "Fabric count",
                                project.fabricPieces.size.toString()
                            )

                            DetailRow(
                                "Rotations are",
                                project.rules.rotation.toString()
                            )

                            DetailRow(
                                "Allow mirror",
                                if (project.rules.allowMirror) "Yes" else "No"
                            )
                        }
                    }

                    Text(
                        text = "Fabrics",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (project.fabricPieces.isEmpty()) {
                        Text(
                            text = "No fabrics added",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        project.fabricPieces.forEachIndexed { index, fabric ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedFabricIndex = index },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Fabric ${index + 1}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "Tap to view shape",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Generated layouts",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (state.generatedLayouts.isEmpty()) {
                        Text(
                            text = "No generated layouts yet",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        state.generatedLayouts.forEachIndexed { index, layout ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onGeneratedLayoutClick(project.id, layout.variantId)
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Layout ${index + 1}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )

                                    Text(
                                        text = "Pattern: ${layout.patternName}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )

                                    Text(
                                        text = "Size: ${layout.patternSize}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = onViewPatternSuggestionsClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Choose a pattern")
                    }
                }

                val selectedFabric =
                    selectedFabricIndex?.let { index -> project.fabricPieces.getOrNull(index) }

                if (selectedFabric != null) {
                    AlertDialog(
                        onDismissRequest = { selectedFabricIndex = null },
                        title = {
                            Text(
                                text = "Fabric ${selectedFabricIndex!! + 1} shape",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        text = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val rectangleSize = selectedFabric.contour.rectangleSize()
                                if (rectangleSize != null) {
                                    val displaySize1 =
                                        if (rectangleSize.first % 1 == 0.0) rectangleSize.first.toInt() else rectangleSize.first
                                    val displaySize2 =
                                        if (rectangleSize.second % 1 == 0.0) rectangleSize.second.toInt() else rectangleSize.second

                                    Text(
                                        text = "$displaySize1 x $displaySize2 mm",
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }

                                LayoutPreview(
                                    fabric = selectedFabric.contour,
                                    piecesById = emptyMap(),
                                    piecesByIdSeamAllowance = emptyMap(),
                                    placements = emptyList(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(320.dp),
                                    fabricIndex = selectedFabricIndex ?: 0,
                                    seamAllowance = project.seamAllowance
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = { selectedFabricIndex = null }) {
                                Text("Close")
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }

                if (showDeleteProjectDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteProjectDialog = false },
                        title = {
                            Text(
                                text = "Delete project?",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        text = {
                            Text(
                                text = "This will permanently delete the project and all generated layouts connected to it.",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDeleteProjectDialog = false
                                    onDeleteProjectClick()
                                }
                            ) {
                                Text("Delete")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDeleteProjectDialog = false }) {
                                Text("Cancel")
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }

                if (state.isEditProjectDialogOpen) {
                    AlertDialog(
                        onDismissRequest = onCloseEditProjectDialog,
                        title = {
                            Text(
                                text = "Edit project",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        text = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ProjectEditTextInput(
                                    label = "Name:",
                                    value = state.editDraft.name,
                                    onValueChange = onEditedProjectNameChanged,
                                    onlyNumbers = false,
                                    placeholder = "Project name"
                                )

                                Text(
                                    text = "Are pattern rotations allowed?",
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                ProjectEditRotationOption(
                                    label = "No",
                                    selected = state.editDraft.rotationOption == UiRotationOption.NONE,
                                    onClick = { onEditedRotationChanged(UiRotationOption.NONE) }
                                )

                                ProjectEditRotationOption(
                                    label = "Only vertically (keep Weft and Warp)",
                                    selected = state.editDraft.rotationOption == UiRotationOption.BY180,
                                    onClick = { onEditedRotationChanged(UiRotationOption.BY180) }
                                )

                                ProjectEditRotationOption(
                                    label = "By 90° at a time",
                                    selected = state.editDraft.rotationOption == UiRotationOption.BY90,
                                    onClick = { onEditedRotationChanged(UiRotationOption.BY90) }
                                )

                                ProjectEditTextInput(
                                    label = "Seam allowance (mm):",
                                    value = state.editDraft.seamAllowance,
                                    onValueChange = onEditedSeamAllowanceChanged,
                                    onlyNumbers = true,
                                    placeholder = "0"
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = state.editDraft.allowMirror,
                                        onCheckedChange = onEditedAllowMirrorChanged
                                    )
                                    Text(
                                        text = "Allow pattern mirroring",
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            Button(onClick = onSaveEditedProject) {
                                Text("Save")
                            }
                        },
                        dismissButton = {
                            Button(onClick = onCloseEditProjectDialog) {
                                Text("Cancel")
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.background
                    )
                }

                if (state.isDeleteLayoutsAfterEditDialogOpen) {
                    AlertDialog(
                        onDismissRequest = onCloseDeleteLayoutsAfterEditDialog,
                        title = {
                            Text(
                                text = "Generated layouts will be deleted",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        text = {
                            Text(
                                text = "Changing seam allowance, rotation, or mirror settings makes existing nesting results invalid. All generated layouts for this project will be deleted after saving.",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        confirmButton = {
                            Button(onClick = onConfirmSaveEditedProjectWithLayoutDeletion) {
                                Text("Continue")
                            }
                        },
                        dismissButton = {
                            Button(onClick = onCloseDeleteLayoutsAfterEditDialog) {
                                Text("Cancel")
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ProjectEditRotationOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ProjectEditTextInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onlyNumbers: Boolean = true,
    placeholder: String = ""
) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(15.dp),
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        },
        keyboardOptions = if (onlyNumbers) {
            androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        } else {
            androidx.compose.foundation.text.KeyboardOptions.Default
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}