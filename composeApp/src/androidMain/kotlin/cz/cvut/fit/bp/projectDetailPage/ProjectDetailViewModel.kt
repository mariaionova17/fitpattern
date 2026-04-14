package cz.cvut.fit.bp.projectDetailPage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fit.bp.projectCreation.ProjectCreationUiState
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
}
