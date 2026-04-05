package cz.cvut.fit.bp.patternSuggestionsPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.cvut.fit.bp.projectsPage.ProjectsViewModel
import domain.repository.PatternRepository
import domain.repository.ProjectRepository

class PatternSuggestionsViewModelFactory(
    private val patternRepository: PatternRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatternSuggestionsViewModel::class.java)) {
            return PatternSuggestionsViewModel(patternRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}