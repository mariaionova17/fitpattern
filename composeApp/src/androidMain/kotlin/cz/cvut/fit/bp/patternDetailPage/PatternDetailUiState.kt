package cz.cvut.fit.bp.patternDetailPage

import domain.pattern.Pattern

data class PatternDetailUiState(
    val isLoading: Boolean = false,
    val pattern: Pattern? = null,
    val errorMessage: String? = null
)