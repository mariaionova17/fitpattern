package cz.cvut.fit.bp.projectDetailPage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fit.bp.imageRecognition.markerDetection.Marker
import cz.cvut.fit.bp.imageRecognition.polygonDetection.CvPoint
import cz.cvut.fit.bp.projectCreation.FabricDraft
import cz.cvut.fit.bp.projectCreation.FabricImageStep
import cz.cvut.fit.bp.projectCreation.FabricInputMode
import cz.cvut.fit.bp.projectCreation.ProjectCreationUiState
import cz.cvut.fit.bp.projectCreation.UiRotationOption
import cz.cvut.fit.bp.projectCreation.toImageFabric
import cz.cvut.fit.bp.projectCreation.toManualFabric
import cz.cvut.fit.bp.projectCreation.toRotationRules
import domain.repository.NestingResultRepository
import domain.repository.PatternRepository
import domain.repository.ProjectRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class ProjectDetailViewModel(private val projectRepository: ProjectRepository, private val patternRepository: PatternRepository, private val nestingResultRepository: NestingResultRepository) : ViewModel() {
    var uiState by mutableStateOf(ProjectDetailUiState())
        private set

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val project = projectRepository.getProjectById(projectId)
                    ?: error("Project not found")

                val nestingResults = nestingResultRepository.getNestingResultsByProject(projectId)

                val generatedLayouts = nestingResults.map { nestingResult ->
                    async {
                        val (patternName, variantId) =
                            nestingResultRepository.getPatternNameAndVariantIdFromNestingResult(
                                nestingResult.id
                            )
                        val patternSize = patternRepository.getSizeByVariantId(variantId) ?: ""

                        GeneratedLayoutCardUi(
                            nestingResultId = nestingResult.id,
                            patternName = patternName,
                            patternSize = patternSize,
                            variantId = variantId
                        )
                    }
                }.awaitAll()

                uiState = uiState.copy(
                    isLoading = false,
                    project = project,
                    generatedLayouts = generatedLayouts
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun deleteProject(onDeleted: () -> Unit) {
        val projectId = uiState.project?.id ?: return

        viewModelScope.launch {
            try {
                uiState = uiState.copy(
                    isLoading = true,
                    errorMessage = null
                )

                nestingResultRepository.deleteNestingResultsByProject(projectId)
                projectRepository.deleteProject(projectId)

                uiState = uiState.copy(isLoading = false)
                onDeleted()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun openEditProjectDialog() {
        val project = uiState.project ?: return

        val seamAllowanceText =
            if (project.seamAllowance % 1 == 0.0) {
                project.seamAllowance.toInt().toString()
            } else {
                project.seamAllowance.toString()
            }

        uiState = uiState.copy(
            isEditProjectDialogOpen = true,
            editDraft = ProjectEditDraft(
                name = project.name,
                seamAllowance = seamAllowanceText,
                rotationOption = projectRotationToUi(project.rules.rotation.toString()),
                allowMirror = project.rules.allowMirror
            ),
            errorMessage = null
        )
    }

    fun closeEditProjectDialog() {
        uiState = uiState.copy(
            isEditProjectDialogOpen = false
        )
    }

    fun onEditedProjectNameChanged(value: String) {
        uiState = uiState.copy(
            editDraft = uiState.editDraft.copy(name = value)
        )
    }

    fun onEditedSeamAllowanceChanged(value: String) {
        uiState = uiState.copy(
            editDraft = uiState.editDraft.copy(seamAllowance = value)
        )
    }

    fun onEditedRotationChanged(value: UiRotationOption) {
        uiState = uiState.copy(
            editDraft = uiState.editDraft.copy(rotationOption = value)
        )
    }

    fun onEditedAllowMirrorChanged(value: Boolean) {
        uiState = uiState.copy(
            editDraft = uiState.editDraft.copy(allowMirror = value)
        )
    }

    fun closeDeleteLayoutsAfterEditDialog() {
        uiState = uiState.copy(
            isDeleteLayoutsAfterEditDialogOpen = false
        )
    }

    fun saveEditedProject() {
        val currentProject = uiState.project ?: return
        val draft = uiState.editDraft

        val currentSeamAllowanceText =
            if (currentProject.seamAllowance % 1 == 0.0) {
                currentProject.seamAllowance.toInt().toString()
            } else {
                currentProject.seamAllowance.toString()
            }

        val currentRotationOption =
            projectRotationToUi(currentProject.rules.rotation.toString())

        val structuralChange =
            draft.seamAllowance != currentSeamAllowanceText ||
                    draft.rotationOption != currentRotationOption ||
                    draft.allowMirror != currentProject.rules.allowMirror

        val shouldWarn =
            structuralChange && uiState.generatedLayouts.isNotEmpty()

        uiState = uiState.copy(
            isEditProjectDialogOpen = false,
            isDeleteLayoutsAfterEditDialogOpen = shouldWarn
        )

        if (!shouldWarn) {
            saveProjectChanges(
                name = draft.name,
                seamAllowance = draft.seamAllowance,
                rotation = draft.rotationOption,
                allowMirror = draft.allowMirror,
                deleteGeneratedLayouts = structuralChange
            )
        }
    }

    fun confirmSaveEditedProjectWithLayoutDeletion() {
        val draft = uiState.editDraft

        uiState = uiState.copy(
            isDeleteLayoutsAfterEditDialogOpen = false
        )

        saveProjectChanges(
            name = draft.name,
            seamAllowance = draft.seamAllowance,
            rotation = draft.rotationOption,
            allowMirror = draft.allowMirror,
            deleteGeneratedLayouts = true
        )
    }

    fun saveProjectChanges(
        name: String,
        seamAllowance: String,
        rotation: UiRotationOption,
        allowMirror: Boolean,
        deleteGeneratedLayouts: Boolean
    ) {
        val currentProject = uiState.project ?: return

        viewModelScope.launch {
            try {
                uiState = uiState.copy(
                    isLoading = true,
                    errorMessage = null
                )

                val seamAllowanceValue = seamAllowance.toDoubleOrNull()
                    ?: error("Invalid seam allowance")

                if (name.isBlank()) {
                    error("Project name cannot be empty")
                }

                val updatedProject = currentProject.copy(
                    name = name,
                    seamAllowance = seamAllowanceValue,
                    rules = currentProject.rules.copy(
                        rotation = rotation.toRotationRules(),
                        allowMirror = allowMirror
                    )
                )

                projectRepository.saveProject(updatedProject)

                if (deleteGeneratedLayouts) {
                    nestingResultRepository.deleteNestingResultsByProject(updatedProject.id)
                }

                val refreshedGeneratedLayouts =
                    if (deleteGeneratedLayouts) {
                        emptyList()
                    } else {
                        val nestingResults =
                            nestingResultRepository.getNestingResultsByProject(updatedProject.id)

                        nestingResults.map { nestingResult ->
                            async {
                                val (patternName, variantId) =
                                    nestingResultRepository.getPatternNameAndVariantIdFromNestingResult(
                                        nestingResult.id
                                    )
                                val patternSize =
                                    patternRepository.getSizeByVariantId(variantId) ?: ""

                                GeneratedLayoutCardUi(
                                    nestingResultId = nestingResult.id,
                                    patternName = patternName,
                                    patternSize = patternSize,
                                    variantId = variantId
                                )
                            }
                        }.awaitAll()
                    }

                uiState = uiState.copy(
                    isLoading = false,
                    project = updatedProject,
                    generatedLayouts = refreshedGeneratedLayouts,
                    errorMessage = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }

    }
    fun openFabricOptionsDialog() {
        uiState = uiState.copy(
            isFabricOptionsDialogOpen = true,
            errorMessage = null
        )
    }

    fun closeFabricOptionsDialog() {
        uiState = uiState.copy(
            isFabricOptionsDialogOpen = false
        )
    }

    fun startManualFabricAdd() {
        uiState = uiState.copy(
            isFabricOptionsDialogOpen = false,
            isManualFabricDialogOpen = true,
            fabricDraft = FabricDraft(inputMode = FabricInputMode.MANUAL),
            errorMessage = null
        )
    }
    fun closeManualFabricDialog() {
        uiState = uiState.copy(
            isManualFabricDialogOpen = false,
            fabricDraft = FabricDraft(),
            currentImageStep = FabricImageStep.NONE,
            errorMessage = null
        )
    }

    fun onFabricWidthChanged(value: String) {
        uiState = uiState.copy(
            fabricDraft = uiState.fabricDraft.copy(widthMm = value)
        )
    }

    fun onFabricLengthChanged(value: String) {
        uiState = uiState.copy(
            fabricDraft = uiState.fabricDraft.copy(lengthMm = value)
        )
    }

    fun onFabricWarpChanged(value: Boolean) {
        uiState = uiState.copy(
            fabricDraft = uiState.fabricDraft.copy(warpAlongWidth = value)
        )
    }

    fun saveManualFabricDraft() {
        try {
            val fabric = uiState.fabricDraft.toManualFabric()
            stageFabricMutation(PendingFabricMutation.Add(fabric))
        } catch (e: Exception) {
            uiState = uiState.copy(errorMessage = e.message)
        }
    }

    fun requestDeleteFabric(index: Int) {
        stageFabricMutation(PendingFabricMutation.Delete(index))
    }

    fun closeDeleteLayoutsAfterFabricChangeDialog() {
        uiState = uiState.copy(
            isDeleteLayoutsAfterFabricChangeDialogOpen = false,
            pendingFabricMutation = null
        )
    }

    fun confirmPendingFabricMutationWithLayoutDeletion() {
        applyPendingFabricMutation(deleteLayouts = true)
    }

    private fun stageFabricMutation(mutation: PendingFabricMutation) {
        val shouldWarn = uiState.generatedLayouts.isNotEmpty()

        if (shouldWarn) {
            uiState = uiState.copy(
                pendingFabricMutation = mutation,
                isFabricOptionsDialogOpen = false,
                isManualFabricDialogOpen = false,
                isDeleteLayoutsAfterFabricChangeDialogOpen = true,
                errorMessage = null
            )
        } else {
            uiState = uiState.copy(
                pendingFabricMutation = mutation,
                isFabricOptionsDialogOpen = false,
                isManualFabricDialogOpen = false,
                errorMessage = null
            )
            applyPendingFabricMutation(deleteLayouts = false)
        }
    }

    private fun applyPendingFabricMutation(deleteLayouts: Boolean) {
        val currentProject = uiState.project ?: return
        val mutation = uiState.pendingFabricMutation ?: return

        val updatedFabrics = when (mutation) {
            is PendingFabricMutation.Add -> {
                currentProject.fabricPieces + mutation.fabric
            }

            is PendingFabricMutation.Delete -> {
                if (currentProject.fabricPieces.size <= 1) {
                    uiState = uiState.copy(
                        isDeleteLayoutsAfterFabricChangeDialogOpen = false,
                        pendingFabricMutation = null,
                        errorMessage = "Project must contain at least one fabric"
                    )
                    return
                }

                currentProject.fabricPieces.toMutableList().apply {
                    removeAt(mutation.index)
                }
            }
        }

        val updatedProject = currentProject.copy(
            fabricPieces = updatedFabrics
        )

        viewModelScope.launch {
            try {
                uiState = uiState.copy(
                    isLoading = true,
                    errorMessage = null
                )

                projectRepository.saveProject(updatedProject)

                if (deleteLayouts) {
                    nestingResultRepository.deleteNestingResultsByProject(updatedProject.id)
                }

                uiState = uiState.copy(
                    isLoading = false,
                    project = updatedProject,
                    generatedLayouts = if (deleteLayouts) emptyList() else uiState.generatedLayouts,
                    isFabricOptionsDialogOpen = false,
                    isManualFabricDialogOpen = false,
                    isDeleteLayoutsAfterFabricChangeDialogOpen = false,
                    fabricDraft = FabricDraft(),
                    currentImageStep = FabricImageStep.NONE,
                    pendingFabricMutation = null,
                    selectedFabricIndexForAction = null,
                    errorMessage = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun startImageFabricAdd() {
        uiState = uiState.copy(
            isFabricOptionsDialogOpen = false,
            isManualFabricDialogOpen = false,
            fabricDraft = FabricDraft(inputMode = FabricInputMode.IMAGE),
            currentImageStep = FabricImageStep.NONE,
            errorMessage = null
        )
    }

    fun setImageUri(uri: String) {
        uiState = uiState.copy(
            fabricDraft = uiState.fabricDraft.copy(
                inputMode = FabricInputMode.IMAGE,
                imageUri = uri,
                resourceImageName = null,
                detectedContour = null,
                editedContour = null,
                detectedA4Marker = null,
                editedA4Marker = null,
                errorMessage = null
            ),
            currentImageStep = FabricImageStep.EDIT_CONTOUR,
            errorMessage = null
        )
    }

    fun setResourceImage(name: String) {
        uiState = uiState.copy(
            fabricDraft = uiState.fabricDraft.copy(
                inputMode = FabricInputMode.IMAGE,
                imageUri = null,
                resourceImageName = name,
                detectedContour = null,
                editedContour = null,
                detectedA4Marker = null,
                editedA4Marker = null,
                errorMessage = null
            ),
            currentImageStep = FabricImageStep.EDIT_CONTOUR,
            errorMessage = null
        )
    }

    fun confirmEditedContour(contour: List<CvPoint>) {
        uiState = uiState.copy(
            fabricDraft = uiState.fabricDraft.copy(
                editedContour = contour,
                errorMessage = null
            ),
            currentImageStep = FabricImageStep.EDIT_A4,
            errorMessage = null
        )
    }

    fun confirmEditedA4(marker: Marker) {
        uiState = uiState.copy(
            fabricDraft = uiState.fabricDraft.copy(
                editedA4Marker = marker,
                errorMessage = null
            ),
            currentImageStep = FabricImageStep.EDIT_WARP,
            errorMessage = null
        )
    }

    fun confirmWarpTilt(degrees: Double) {
        uiState = uiState.copy(
            fabricDraft = uiState.fabricDraft.copy(
                warpAngleDegreesFromVertical = degrees,
                errorMessage = null
            ),
            errorMessage = null
        )
    }

    fun finishImageFabricAdd() {
        try {
            val fabric = uiState.fabricDraft.toImageFabric()
            stageFabricMutation(PendingFabricMutation.Add(fabric))
        } catch (e: Exception) {
            uiState = uiState.copy(errorMessage = e.message)
        }
    }

    fun clearImageFabricDraft() {
        uiState = uiState.copy(
            fabricDraft = FabricDraft(),
            currentImageStep = FabricImageStep.NONE,
            errorMessage = null
        )
    }
}

private fun projectRotationToUi(rotation: String): UiRotationOption {
    return when (rotation) {
        "NONE" -> UiRotationOption.NONE
        "BY180" -> UiRotationOption.BY180
        "BY90" -> UiRotationOption.BY90
        else -> UiRotationOption.NONE
    }
}
