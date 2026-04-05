package cz.cvut.fit.bp.patternSuggestionsPage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fit.bp.projectsPage.ProjectsUiState
import domain.repository.PatternRepository
import kotlinx.coroutines.launch

class PatternSuggestionsViewModel(private val patternRepository: PatternRepository): ViewModel() {
    var uiState by mutableStateOf(PatternSuggestionsUiState())
        private set


    fun loadPatterns() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                val patterns = patternRepository.getAllPatterns()
                uiState = uiState.copy(
                    isLoading = false,
                    patterns = patterns
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