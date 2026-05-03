package cz.cvut.fit.bp.layoutPage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.repository.PatternRepository
import domain.repository.ProjectRepository
import domain.repository.NestingResultRepository
import domain.solver.BetterSolver
import domain.solver.NestingProblem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class LayoutViewModel(
    private val patternRepository: PatternRepository,
    private val projectRepository: ProjectRepository,
    private val nestingResultRepository: NestingResultRepository,
    private val solver: BetterSolver,
    private val solverDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    var uiState by mutableStateOf(LayoutUiState())
        private set

    fun openOrGenerateLayout(projectId: String, variantId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true,
                generationProgress = 0f,
                errorMessage = null
            )

            try {
                val project = projectRepository.getProjectById(projectId)
                    ?: error("Project not found")

                val pieces = patternRepository.getPatternByVariantId(variantId)
                val patternSize = patternRepository.getSizeByVariantId(variantId) ?: ""

                val savedResults =
                    nestingResultRepository.getNestingResultsByProjectAndVariant(
                        projectId = projectId,
                        patternVariantId = variantId
                    )

                val savedResult = savedResults.lastOrNull()
                var patternName = ""

                if (savedResult != null) {
                    patternName =
                        nestingResultRepository.getPatternNameAndVariantIdFromNestingResult(
                            savedResult.id
                        ).first
                }

                if (savedResult != null) {
                    uiState = uiState.copy(
                        isLoading = false,
                        project = project,
                        patternPieces = pieces,
                        result = savedResult,
                        hasSavedLayout = true,
                        patternName = patternName,
                        patternSize = patternSize,
                    )
                } else {
                    val problem = NestingProblem(
                        project = project,
                        pieces = pieces
                    )

                    val resultWithoutIds = withContext(solverDispatcher) {
                        solver.solve(problem) { progress ->
                            uiState = uiState.copy(
                                generationProgress = progress
                            )
                        }
                    }

                    val result = resultWithoutIds.copy(
                        id = UUID.randomUUID().toString(),
                        bestLayout = resultWithoutIds.bestLayout.copy(
                            placed = resultWithoutIds.bestLayout.placed.map { placement ->
                                placement.copy(id = UUID.randomUUID().toString())
                            }
                        )
                    )

                    nestingResultRepository.saveNestingResult(
                        nestingResult = result,
                        projectId = projectId,
                        patternVariantId = variantId
                    )
                    patternName = nestingResultRepository.getPatternNameAndVariantIdFromNestingResult(
                        result.id
                    ).first

                    uiState = uiState.copy(
                        isLoading = false,
                        project = project,
                        patternPieces = pieces,
                        result = result,
                        hasSavedLayout = true,
                        patternName = patternName,
                        patternSize = patternSize,
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }
}