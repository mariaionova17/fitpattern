package cz.cvut.fit.bp.patternDetailPage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.repository.PatternRepository
import kotlinx.coroutines.launch

class PatternDetailViewModel(private val patternRepository: PatternRepository) : ViewModel() {
    var uiState by mutableStateOf(PatternDetailUiState())
        private set

    fun loadProject(patternId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                val pattern = patternRepository.getPatternById(patternId)
                uiState = uiState.copy(
                    isLoading = false,
                    pattern = pattern
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
