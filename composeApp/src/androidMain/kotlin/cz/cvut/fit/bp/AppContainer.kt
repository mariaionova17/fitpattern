package cz.cvut.fit.bp

import android.content.Context
import cz.cvut.fit.bp.shareddb.data.db.createDatabase
import cz.cvut.fit.bp.shareddb.data.repository.PatternRepositoryImpl
import cz.cvut.fit.bp.shareddb.data.repository.ProjectRepositoryImpl
import domain.layout.GeometryKernel
import domain.layout.SimpleGeometryKernel
import domain.repository.PatternRepository
import domain.repository.ProjectRepository
import domain.solver.InitialSolver

class AppContainer(context: Context) {
    private val db = createDatabase(context)

    val projectRepository: ProjectRepository = ProjectRepositoryImpl(db)
    val patternRepository: PatternRepository = PatternRepositoryImpl(db)

    val geometryKernel: GeometryKernel = SimpleGeometryKernel()
    val nestingSolver = InitialSolver(geometryKernel)
}