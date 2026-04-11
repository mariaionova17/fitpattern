package cz.cvut.fit.bp.layoutPage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.repository.PatternRepository
import domain.repository.ProjectRepository
import domain.solver.BetterSolver
import domain.solver.InitialSolver
import domain.solver.NestingProblem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LayoutViewModel(
    private val patternRepository: PatternRepository,
    private val projectRepository: ProjectRepository,
    private val solver: BetterSolver
) : ViewModel() {

    var uiState by mutableStateOf(LayoutUiState())
        private set

    fun generateLayout(projectId: String, variantId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val project = projectRepository.getProjectById(projectId)
                    ?: error("Project not found")

                val pieces = patternRepository.getPatternByVariantId(variantId)

                val problem = NestingProblem(
                    project = project,
                    pieces = pieces
                )

                val result = withContext(Dispatchers.Default) {
                    solver.solve(problem)
                }

                uiState = uiState.copy(
                    isLoading = false,
                    project = project,
                    patternPieces = pieces,
                    result = result
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