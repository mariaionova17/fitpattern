package cz.cvut.fit.bp.shareddb.data.repository

import cz.cvut.fit.bp.shareddb.data.dao.NestingResultDao
import cz.cvut.fit.bp.shareddb.data.db.AppDatabase
import cz.cvut.fit.bp.shareddb.data.mapper.toDomain
import cz.cvut.fit.bp.shareddb.data.mapper.toEntities
import domain.geometry.Transformation
import domain.repository.NestingResultRepository
import domain.solver.NestingResult

class NestingResultRepositoryImpl(
    private val db: AppDatabase,
): NestingResultRepository {

    val dao = db.nestingResultDao()

    override suspend fun saveNestingResult(
        nestingResult: NestingResult,
        projectId: String,
        patternVariantId: String
    ) {
        val nextIndex = dao.getMaxLayoutVersionIndex(projectId, patternVariantId) + 1

        val (resultEntity, placedEntities) = nestingResult.toEntities(
            projectId = projectId,
            patternVariantId = patternVariantId,
            layoutVersionIndex = nextIndex,
        )

        dao.insertNestingResult(resultEntity)
        dao.insertPlacedPieces(placedEntities)

    }

    override suspend fun getNestingResultsByProjectAndVariant(
        projectId: String,
        patternVariantId: String
    ): List<NestingResult> {
        val resultEntities = dao.getResultsForProjectAndVariant(projectId, patternVariantId)

        return resultEntities.mapNotNull { resultEntity ->
            dao.getNestingResultWithPieces(resultEntity.id)?.toDomain()
        }
    }
    override suspend fun getNestingResultsByProject(
        projectId: String
    ): List<NestingResult> {
        val resultEntities = dao.getResultsForProject(projectId)

        return resultEntities.mapNotNull { resultEntity ->
            dao.getNestingResultWithPieces(resultEntity.id)?.toDomain()
        }
    }

    override suspend fun getPatternNameAndVariantIdFromNestingResult(
        nestingResultId: String
    ): Pair<String, String> {
        val name = dao.getPatternNameFromNestingResult(nestingResultId)
        val size = dao.getPatternVariantIdFromNestingResult(nestingResultId)

        return if (name != null && size != null) Pair(name, size) else Pair("","")
    }

    override suspend fun deleteNestingResultsByProject(projectId: String) {
        dao.deletePlacedPiecesByProjectId(projectId)
        dao.deleteNestingResultsByProjectId(projectId)
    }

}