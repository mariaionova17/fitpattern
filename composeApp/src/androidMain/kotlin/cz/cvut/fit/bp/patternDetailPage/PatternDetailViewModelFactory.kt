package cz.cvut.fit.bp.patternDetailPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import domain.repository.PatternRepository

class PatternDetailViewModelFactory(
    private val patternRepository: PatternRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatternDetailViewModel::class.java)) {
            return PatternDetailViewModel(patternRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}