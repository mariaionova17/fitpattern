package cz.cvut.fit.bp.patternSuggestionsPage

import domain.pattern.Pattern
import domain.project.Project

data class PatternSuggestionsUiState(
    val isLoading: Boolean = false,
    val patterns: List<Pattern> = emptyList(),
    val errorMessage: String? = null
)