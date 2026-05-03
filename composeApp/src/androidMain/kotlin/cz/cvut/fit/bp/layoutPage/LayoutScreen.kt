package cz.cvut.fit.bp.layoutPage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cz.cvut.fit.bp.LayoutPreview
import cz.cvut.fit.bp.R
import domain.geometry.setSeamAllowance
import domain.pattern.PatternPiece
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayoutScreen(
    state: LayoutUiState,
    onBack: () -> Unit,
    onHome: () -> Unit
) {
    ScaffoldWithTopBar(
        title = "Generated layout",
        onBack = onBack,
        onHome = onHome
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        progress = { state.generationProgress }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${(state.generationProgress * 100).toInt()}%",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            state.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            state.project == null || state.result == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No layout data",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            else -> {
                val project = state.project
                val result = state.result

                val notPlacedCount = state.patternPieces.size - result.bestLayout.placed.size

                var showNotAllPlacedDialog by remember(result.id) {
                    mutableStateOf(notPlacedCount > 0)
                }

                val piecesById: Map<String, PatternPiece> =
                    state.patternPieces.associateBy { it.id }

                val piecesByIdSeamAllowance: Map<String, PatternPiece> =
                    state.patternPieces
                        .map { piece ->
                            piece.copy(shape = piece.shape.setSeamAllowance(project.seamAllowance))
                        }
                        .associateBy { it.id }

                if (showNotAllPlacedDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showNotAllPlacedDialog = false
                        },
                        title = {
                            Text("Not all pieces were placed")
                        },
                        text = {
                            Text(
                                "${state.patternPieces.size - result.bestLayout.placed.size} out of ${state.patternPieces.size} pieces could not be placed on the selected fabric."
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showNotAllPlacedDialog = false
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.background
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Project: ${project.name}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    item {
                        Text(
                            text = "Pattern: ${state.patternName}, size: ${state.patternSize}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    item {
                        Text(
                            text = "Placed pieces: ${result.bestLayout.placed.size}, Not placed: ${state.patternPieces.size - result.bestLayout.placed.size}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    itemsIndexed(project.fabricPieces) { index, fabricPiece ->
                        Surface(
                            tonalElevation = 2.dp,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Fabric ${index + 1}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                LayoutPreview(
                                    fabric = fabricPiece.contour,
                                    piecesById = piecesById,
                                    piecesByIdSeamAllowance = piecesByIdSeamAllowance,
                                    placements = result.bestLayout.placed,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(320.dp),
                                    fabricIndex = index,
                                    seamAllowance = project.seamAllowance
                                )
                                val placementsForThisFabric =
                                    result.bestLayout.placed.filter { it.fabricPieceIndex == index }

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    placementsForThisFabric.forEachIndexed { pieceIndex, placement ->
                                        val pieceName = piecesById[placement.pieceId]?.name ?: "Unknown piece"

                                        Text(
                                            text = "${pieceIndex + 1}. $pieceName",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithTopBar(
    title: String,
    onBack: () -> Unit,
    onHome: () -> Unit,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = title,
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
                    colors = TopAppBarDefaults.topAppBarColors(
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
        content = content,
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
    )
}