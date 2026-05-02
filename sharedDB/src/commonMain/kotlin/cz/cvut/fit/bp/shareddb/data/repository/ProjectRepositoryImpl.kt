package cz.cvut.fit.bp.shareddb.data.repository

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import cz.cvut.fit.bp.shareddb.data.dao.NestingResultDao
import cz.cvut.fit.bp.shareddb.data.dao.ProjectDao
import cz.cvut.fit.bp.shareddb.data.db.AppDatabase
import cz.cvut.fit.bp.shareddb.data.entity.ProjectEntity
import cz.cvut.fit.bp.shareddb.data.mapper.toFabricPiece
import cz.cvut.fit.bp.shareddb.data.mapper.toFabricPieceEntity
import cz.cvut.fit.bp.shareddb.data.mapper.toPlacementRules
import cz.cvut.fit.bp.shareddb.data.mapper.toRulesEntity
import domain.project.Project
import domain.repository.ProjectRepository

class ProjectRepositoryImpl(
    private val dao: ProjectDao,
    private val nestingResultDao: NestingResultDao,
    private val runInTransaction: suspend (suspend () -> Unit) -> Unit = { block -> block() }
) : ProjectRepository {

    constructor(db: AppDatabase) : this(
        dao = db.projectDao(),
        nestingResultDao = db.nestingResultDao(),
        runInTransaction = { block ->
            db.useWriterConnection { transactor ->
                transactor.immediateTransaction {
                    block()
                }
            }
        }
    )

    override suspend fun saveProject(project: Project) {
        dao.insertProject(
            ProjectEntity(
                id = project.id,
                name = project.name,
                seamAllowance = project.seamAllowance
            )
        )

        dao.insertRules(project.rules.toRulesEntity(project.id))

        dao.deleteFabricsByProjectId(project.id)
        dao.insertFabrics(project.fabricPieces.map { it.toFabricPieceEntity(project.id) })
    }

    override suspend fun getProjectById(id: String): Project? {
        val projectEntity = dao.getProjectById(id) ?: return null
        val rulesEntity = dao.getRulesByProjectId(id) ?: return null
        val fabricEntities = dao.getFabricsByProjectId(id)

        return Project(
            id = projectEntity.id,
            name = projectEntity.name,
            fabricPieces = fabricEntities.map { it.toFabricPiece() },
            rules = rulesEntity.toPlacementRules(),
            seamAllowance = projectEntity.seamAllowance
        )
    }

    override suspend fun getAllProjects(): List<Project> {
        return dao.getAllProjects().mapNotNull { projectEntity ->
            val rulesEntity = dao.getRulesByProjectId(projectEntity.id) ?: return@mapNotNull null
            val fabricEntities = dao.getFabricsByProjectId(projectEntity.id)

            Project(
                id = projectEntity.id,
                name = projectEntity.name,
                fabricPieces = fabricEntities.map { it.toFabricPiece() },
                rules = rulesEntity.toPlacementRules(),
                seamAllowance = projectEntity.seamAllowance
            )
        }
    }

    override suspend fun deleteProject(id: String) {
        runInTransaction {
            nestingResultDao.deletePlacedPiecesByProjectId(id)
            nestingResultDao.deleteNestingResultsByProjectId(id)

            dao.deleteFabricsByProjectId(id)
            dao.deleteRulesByProjectId(id)
            dao.deleteProjectById(id)
        }
    }
}