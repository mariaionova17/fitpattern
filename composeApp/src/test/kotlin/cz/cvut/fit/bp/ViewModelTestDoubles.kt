package cz.cvut.fit.bp

import domain.pattern.Pattern
import domain.pattern.PatternPiece
import domain.project.Project
import domain.repository.NestingResultRepository
import domain.repository.PatternRepository
import domain.repository.ProjectRepository
import domain.solver.NestingResult

class FakePatternRepository : PatternRepository {
    var patternByIdResult: Pattern? = null
    var getPatternByIdException: Exception? = null
    var allPatternsResult: List<Pattern> = emptyList()
    var getAllPatternsException: Exception? = null
    val sizeByVariantId = mutableMapOf<String, String?>()
    val piecesByVariantId = mutableMapOf<String, List<PatternPiece>>()
    val savedPatterns = mutableListOf<Pattern>()
    val deletedPatternIds = mutableListOf<String>()
    val callLog = mutableListOf<String>()

    override suspend fun savePattern(pattern: Pattern) {
        callLog += "savePattern:${pattern.id}"
        savedPatterns += pattern
    }

    override suspend fun getPatternById(id: String): Pattern? {
        callLog += "getPatternById:$id"
        getPatternByIdException?.let { throw it }
        return patternByIdResult
    }

    override suspend fun getPatternByVariantId(variantId: String): List<PatternPiece> {
        callLog += "getPatternByVariantId:$variantId"
        return piecesByVariantId[variantId].orEmpty()
    }

    override suspend fun getSizeByVariantId(variantId: String): String? {
        callLog += "getSizeByVariantId:$variantId"
        return sizeByVariantId[variantId]
    }

    override suspend fun getAllPatterns(): List<Pattern> {
        callLog += "getAllPatterns"
        getAllPatternsException?.let { throw it }
        return allPatternsResult
    }

    override suspend fun deletePattern(id: String) {
        callLog += "deletePattern:$id"
        deletedPatternIds += id
    }
}

class FakeProjectRepository : ProjectRepository {
    var projectByIdResult: Project? = null
    var getProjectByIdException: Exception? = null
    var allProjectsResult: List<Project> = emptyList()
    var getAllProjectsException: Exception? = null
    var saveProjectException: Exception? = null
    var deleteProjectException: Exception? = null
    val savedProjects = mutableListOf<Project>()
    val deletedProjectIds = mutableListOf<String>()
    val callLog = mutableListOf<String>()

    override suspend fun saveProject(project: Project) {
        callLog += "saveProject:${project.id}"
        saveProjectException?.let { throw it }
        savedProjects += project
    }

    override suspend fun getProjectById(id: String): Project? {
        callLog += "getProjectById:$id"
        getProjectByIdException?.let { throw it }
        return projectByIdResult
    }

    override suspend fun getAllProjects(): List<Project> {
        callLog += "getAllProjects"
        getAllProjectsException?.let { throw it }
        return allProjectsResult
    }

    override suspend fun deleteProject(id: String) {
        callLog += "deleteProject:$id"
        deleteProjectException?.let { throw it }
        deletedProjectIds += id
    }
}

class FakeNestingResultRepository : NestingResultRepository {
    val resultsByProjectId = mutableMapOf<String, List<NestingResult>>()
    val resultsByProjectAndVariant = mutableMapOf<Pair<String, String>, List<NestingResult>>()
    val nameAndVariantByResultId = mutableMapOf<String, Pair<String, String>>()
    var getResultsByProjectException: Exception? = null
    var deleteByProjectException: Exception? = null
    val deletedProjectIds = mutableListOf<String>()
    val callLog = mutableListOf<String>()

    override suspend fun saveNestingResult(
        nestingResult: NestingResult,
        projectId: String,
        patternVariantId: String
    ) {
        callLog += "saveNestingResult:${nestingResult.id}"
    }

    override suspend fun getNestingResultsByProjectAndVariant(
        projectId: String,
        patternVariantId: String
    ): List<NestingResult> {
        callLog += "getNestingResultsByProjectAndVariant:$projectId:$patternVariantId"
        return resultsByProjectAndVariant[projectId to patternVariantId].orEmpty()
    }

    override suspend fun getNestingResultsByProject(projectId: String): List<NestingResult> {
        callLog += "getNestingResultsByProject:$projectId"
        getResultsByProjectException?.let { throw it }
        return resultsByProjectId[projectId].orEmpty()
    }

    override suspend fun getPatternNameAndVariantIdFromNestingResult(
        nestingResultId: String
    ): Pair<String, String> {
        callLog += "getPatternNameAndVariantIdFromNestingResult:$nestingResultId"
        return nameAndVariantByResultId[nestingResultId] ?: ("" to "")
    }

    override suspend fun deleteNestingResultsByProject(projectId: String) {
        callLog += "deleteNestingResultsByProject:$projectId"
        deleteByProjectException?.let { throw it }
        deletedProjectIds += projectId
    }
}
