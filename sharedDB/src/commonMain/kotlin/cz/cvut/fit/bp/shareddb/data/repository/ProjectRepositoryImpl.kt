package cz.cvut.fit.bp.shareddb.data.repository

import cz.cvut.fit.bp.shareddb.data.db.AppDatabase
import cz.cvut.fit.bp.shareddb.data.entity.ProjectEntity
import cz.cvut.fit.bp.shareddb.data.mapper.toFabricPiece
import cz.cvut.fit.bp.shareddb.data.mapper.toFabricPieceEntity
import cz.cvut.fit.bp.shareddb.data.mapper.toPlacementRules
import cz.cvut.fit.bp.shareddb.data.mapper.toRulesEntity
import domain.project.Project
import domain.repository.ProjectRepository

class ProjectRepositoryImpl(
    private val db: AppDatabase
) : ProjectRepository {

    override suspend fun saveProject(project: Project) {
        val dao = db.projectDao()

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
        val dao = db.projectDao()

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
        val dao = db.projectDao()

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
        val dao = db.projectDao()
        dao.deleteFabricsByProjectId(id)
        dao.deleteRulesByProjectId(id)
        dao.deleteProjectById(id)
    }
}