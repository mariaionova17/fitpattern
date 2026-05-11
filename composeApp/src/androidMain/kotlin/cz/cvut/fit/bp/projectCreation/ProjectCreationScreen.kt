package cz.cvut.fit.bp.projectCreation

import cz.cvut.fit.bp.R
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectCreationScreen(
    state: ProjectCreationUiState,
    onProjectNameChanged: (String) -> Unit,
    onSeamAllowanceChanged: (String) -> Unit,
    onRotationChanged: (UiRotationOption) -> Unit,
    onAllowMirrorChanged: (Boolean) -> Unit,
    onFabricWidthChanged: (String) -> Unit,
    onFabricLengthChanged: (String) -> Unit,
    onWarpChanged: (Boolean) -> Unit,
    onNextClicked: () -> Unit,
    onManualDialogDismiss: () -> Unit,
    onManualDialogConfirm: () -> Unit,
    onOpenManualDialogue: () -> Unit,
    onOpenSelectImage: () -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "New project",
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
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(horizontal = 40.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            TextInput("Name:", state.projectName, onProjectNameChanged, false, "New project")
//TODO max 30 chars
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Add fabric",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                FabricModeCard(
                    label = "Manually",
                    selected = state.currentFabric.inputMode == FabricInputMode.MANUAL,
                    onClick = onOpenManualDialogue,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.manual),
                        contentDescription = "Manual mode",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(50.dp)
                    )
                }

                FabricModeCard(
                    label = "Add an image",
                    selected = state.currentFabric.inputMode == FabricInputMode.IMAGE,
                    onClick = onOpenSelectImage,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.gallery),
                        contentDescription = "Image mode",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            if (state.isManualDialogOpen) {
                AlertDialog(
                    onDismissRequest = { onManualDialogDismiss() },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ){
                            TextInput("Fabric width (mm):", state.currentFabric.widthMm, onFabricWidthChanged)
                            TextInput("Fabric length (mm):", state.currentFabric.lengthMm, onFabricLengthChanged)

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = state.currentFabric.warpAlongWidth,
                                    onCheckedChange = onWarpChanged
                                )
                                Text("Warp is along fabric's width", color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = onManualDialogConfirm) {
                            Text("Add fabric")
                        }
                    },
                    dismissButton = {
                        Button(onClick = onManualDialogDismiss) {
                            Text("Cancel")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.background
                )
            }

            Text(
                text = "Added fabrics: ${state.addedFabricPieces.size}",
                style = MaterialTheme.typography.titleMedium
            )

            /*state.addedFabricPieces.forEachIndexed { index, _ ->
                Text("Fabric ${index + 1}")
            }*/

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Are pattern rotations allowed?", color = MaterialTheme.colorScheme.onBackground)

            RotationOption(
                label = "No",
                selected = state.rotationOption == UiRotationOption.NONE,
                onClick = { onRotationChanged(UiRotationOption.NONE) },
                textColor = MaterialTheme.colorScheme.onBackground
            )

            RotationOption(
                label = "Only vertically (keep Weft and Warp)",
                selected = state.rotationOption == UiRotationOption.BY180,
                onClick = { onRotationChanged(UiRotationOption.BY180) }
            )

            RotationOption(
                label = "By 90° at a time",
                selected = state.rotationOption == UiRotationOption.BY90,
                onClick = { onRotationChanged(UiRotationOption.BY90) }
            )

            /*RotationOption(
                label = "Yes, by any angle",
                selected = state.rotationOption == UiRotationOption.ANY,
                onClick = { onRotationChanged(UiRotationOption.ANY) }
            )*/

            TextInput("Seam allowance (mm):", state.seamAllowance, onSeamAllowanceChanged, true, "0")

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.allowMirror,
                    onCheckedChange = onAllowMirrorChanged
                )
                Text("Allow pattern mirroring", color = MaterialTheme.colorScheme.onBackground)
            }

            Button(
                onClick = onNextClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
private fun RotationOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(label, color = textColor)
    }
}

@Composable
private fun FabricModeCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TextInput(
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
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        },
        keyboardOptions = if (onlyNumbers) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,

            )
    )
}