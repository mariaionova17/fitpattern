package cz.cvut.fit.bp

import android.content.Context
import cz.cvut.fit.bp.shareddb.data.db.createDatabase
import cz.cvut.fit.bp.shareddb.data.repository.NestingResultRepositoryImpl
import cz.cvut.fit.bp.shareddb.data.repository.PatternRepositoryImpl
import cz.cvut.fit.bp.shareddb.data.repository.ProjectRepositoryImpl
import domain.layout.GeometryKernel
import domain.layout.SimpleGeometryKernel
import domain.repository.NestingResultRepository
import domain.repository.PatternRepository
import domain.repository.ProjectRepository
import domain.solver.BetterSolver
import domain.solver.InitialSolver

class AppContainer(context: Context) {
    private val db = createDatabase(context)

    val projectRepository: ProjectRepository = ProjectRepositoryImpl(db)
    val patternRepository: PatternRepository = PatternRepositoryImpl(db)
    val nestingResultRepository: NestingResultRepository = NestingResultRepositoryImpl(db)

    val geometryKernel: GeometryKernel = SimpleGeometryKernel()
    val nestingSolver = BetterSolver(geometryKernel)
}