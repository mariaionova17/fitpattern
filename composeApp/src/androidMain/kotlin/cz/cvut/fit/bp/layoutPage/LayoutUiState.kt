package cz.cvut.fit.bp.layoutPage

import domain.pattern.Pattern
import domain.pattern.PatternPiece
import domain.project.Project
import domain.solver.NestingResult

data class LayoutUiState(
    val isLoading: Boolean = false,
    val project: Project? = null,
    val patternPieces: List<PatternPiece> = listOf(),
    val result: NestingResult? = null,
    val errorMessage: String? = null,
    val hasSavedLayout: Boolean = false,
    val generationProgress: Float = 0f,
    val patternName: String? = null,
    val patternSize: String? = null

)