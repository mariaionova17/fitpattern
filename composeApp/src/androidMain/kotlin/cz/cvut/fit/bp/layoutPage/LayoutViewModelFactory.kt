package cz.cvut.fit.bp.layoutPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.cvut.fit.bp.patternDetailPage.PatternDetailViewModel
import domain.repository.PatternRepository
import domain.repository.ProjectRepository
import domain.solver.BetterSolver
import domain.solver.InitialSolver

class LayoutViewModelFactory(
    private val patternRepository: PatternRepository,
    private val projectRepository: ProjectRepository,
    private val solver: BetterSolver,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LayoutViewModel::class.java)) {
            return LayoutViewModel(patternRepository, projectRepository, solver) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}