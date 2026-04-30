package cz.cvut.fit.bp.shareddb.data.repository

import cz.cvut.fit.bp.shareddb.data.dao.NestingResultDao
import cz.cvut.fit.bp.shareddb.data.dao.PatternDao
import cz.cvut.fit.bp.shareddb.data.dao.ProjectDao
import cz.cvut.fit.bp.shareddb.data.entity.FabricPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.LayoutPlacedPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.NestingResultEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternVariantEntity
import cz.cvut.fit.bp.shareddb.data.entity.PlacementRulesEntity
import cz.cvut.fit.bp.shareddb.data.entity.ProjectEntity
import cz.cvut.fit.bp.shareddb.data.relation.NestingResultWithPieces

internal class FakeProjectDao : ProjectDao {
    val projects = linkedMapOf<String, ProjectEntity>()
    val rules = linkedMapOf<String, PlacementRulesEntity>()
    val fabricsByProjectId = linkedMapOf<String, MutableList<FabricPieceEntity>>()
    val callLog = mutableListOf<String>()

    override suspend fun insertProject(project: ProjectEntity) {
        callLog += "insertProject:${project.id}"
        projects[project.id] = project
    }

    override suspend fun insertRules(rules: PlacementRulesEntity) {
        callLog += "insertRules:${rules.projectId}"
        this.rules[rules.projectId] = rules
    }

    override suspend fun insertFabrics(fabrics: List<FabricPieceEntity>) {
        val projectId = fabrics.firstOrNull()?.projectId ?: "empty"
        callLog += "insertFabrics:$projectId"
        if (fabrics.isNotEmpty()) {
            fabricsByProjectId[fabrics.first().projectId] = fabrics.toMutableList()
        }
    }

    override suspend fun getProjectById(projectId: String): ProjectEntity? = projects[projectId]

    override suspend fun getRulesByProjectId(projectId: String): PlacementRulesEntity? = rules[projectId]

    override suspend fun getFabricsByProjectId(projectId: String): List<FabricPieceEntity> =
        fabricsByProjectId[projectId]?.toList() ?: emptyList()

    override suspend fun getAllProjects(): List<ProjectEntity> = projects.values.toList()

    override suspend fun deleteFabricsByProjectId(projectId: String) {
        callLog += "deleteFabrics:$projectId"
        fabricsByProjectId.remove(projectId)
    }

    override suspend fun deleteRulesByProjectId(projectId: String) {
        callLog += "deleteRules:$projectId"
        rules.remove(projectId)
    }

    override suspend fun deleteProjectById(projectId: String) {
        callLog += "deleteProject:$projectId"
        projects.remove(projectId)
    }
}

internal class FakePatternDao : PatternDao {
    val patterns = linkedMapOf<String, PatternEntity>()
    val variants = linkedMapOf<String, PatternVariantEntity>()
    val pieces = linkedMapOf<String, PatternPieceEntity>()
    val callLog = mutableListOf<String>()

    override suspend fun insertPattern(pattern: PatternEntity) {
        callLog += "insertPattern:${pattern.id}"
        patterns[pattern.id] = pattern
    }

    override suspend fun insertPatterns(patterns: List<PatternEntity>) {
        patterns.forEach { pattern ->
            this.patterns[pattern.id] = pattern
        }
    }

    override suspend fun insertVariants(variants: List<PatternVariantEntity>) {
        callLog += "insertVariants:${variants.size}"
        variants.forEach { variant ->
            this.variants[variant.variantId] = variant
        }
    }

    override suspend fun insertPieces(pieces: List<PatternPieceEntity>) {
        callLog += "insertPieces:${pieces.size}"
        pieces.forEach { piece ->
            this.pieces[piece.id] = piece
        }
    }

    override suspend fun getPatternById(patternId: String): PatternEntity? = patterns[patternId]

    override suspend fun getSizeByVariantId(variantId: String): String? = variants[variantId]?.size

    override suspend fun getVariantsByPatternId(patternId: String): List<PatternVariantEntity> =
        variants.values.filter { it.patternId == patternId }

    override suspend fun getPiecesByVariantId(variantId: String): List<PatternPieceEntity> =
        pieces.values.filter { it.patternVariantId == variantId }

    override suspend fun getAllPatterns(): List<PatternEntity> = patterns.values.toList()

    override suspend fun deletePatternById(patternId: String) {
        callLog += "deletePattern:$patternId"
        patterns.remove(patternId)
    }
}

internal class FakeNestingResultDao : NestingResultDao {
    val results = linkedMapOf<String, NestingResultEntity>()
    val placedPiecesByResultId = linkedMapOf<String, MutableList<LayoutPlacedPieceEntity>>()
    val relationsById = linkedMapOf<String, NestingResultWithPieces?>()
    val maxIndexByProjectVariant = linkedMapOf<Pair<String, String>, Int>()
    val patternVariantIdByResultId = linkedMapOf<String, String?>()
    val patternNameByResultId = linkedMapOf<String, String?>()
    val callLog = mutableListOf<String>()

    override suspend fun insertNestingResult(result: NestingResultEntity) {
        callLog += "insertNestingResult:${result.id}"
        results[result.id] = result
    }

    override suspend fun insertPlacedPieces(pieces: List<LayoutPlacedPieceEntity>) {
        val resultId = pieces.firstOrNull()?.nestingResultId ?: "empty"
        callLog += "insertPlacedPieces:$resultId"
        if (pieces.isNotEmpty()) {
            placedPiecesByResultId[resultId] = pieces.toMutableList()
        }
    }

    override suspend fun getNestingResultWithPieces(nestingResultId: String): NestingResultWithPieces? =
        relationsById[nestingResultId]

    override suspend fun getResultsForProjectAndVariant(
        projectId: String,
        patternVariantId: String
    ): List<NestingResultEntity> =
        results.values
            .filter { it.projectId == projectId && it.patternVariantId == patternVariantId }
            .sortedBy { it.layoutVersionIndex }

    override suspend fun getResultsForProject(projectId: String): List<NestingResultEntity> =
        results.values
            .filter { it.projectId == projectId }
            .sortedBy { it.layoutVersionIndex }

    override suspend fun deleteNestingResult(nestingResultId: String) {
        callLog += "deleteNestingResult:$nestingResultId"
        results.remove(nestingResultId)
        placedPiecesByResultId.remove(nestingResultId)
        relationsById.remove(nestingResultId)
    }

    override suspend fun getMaxLayoutVersionIndex(projectId: String, patternVariantId: String): Int =
        maxIndexByProjectVariant[projectId to patternVariantId] ?: 0

    override suspend fun getPatternVariantIdFromNestingResult(nestingResultId: String): String? =
        patternVariantIdByResultId[nestingResultId]

    override suspend fun getPatternNameFromNestingResult(nestingResultId: String): String? =
        patternNameByResultId[nestingResultId]

    override suspend fun deletePlacedPiecesByProjectId(projectId: String) {
        callLog += "deletePlacedPiecesByProject:$projectId"
        val resultIds = results.values.filter { it.projectId == projectId }.map { it.id }.toSet()
        resultIds.forEach { placedPiecesByResultId.remove(it) }
    }

    override suspend fun deleteNestingResultsByProjectId(projectId: String) {
        callLog += "deleteNestingResultsByProject:$projectId"
        val idsToRemove = results.values.filter { it.projectId == projectId }.map { it.id }
        idsToRemove.forEach {
            results.remove(it)
            relationsById.remove(it)
        }
    }
}
