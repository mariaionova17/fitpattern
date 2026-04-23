package domain.repository

import domain.pattern.Pattern
import domain.solver.NestingResult

interface NestingResultRepository {
    suspend fun saveNestingResult(nestingResult: NestingResult, projectId: String, patternVariantId: String)
    suspend fun getNestingResultsByProjectAndVariant(
        projectId: String,
        patternVariantId: String
    ): List<NestingResult>

    suspend fun getNestingResultsByProject(projectId: String): List<NestingResult>

    suspend fun getPatternNameAndVariantIdFromNestingResult(
        nestingResultId: String
    ): Pair<String, String>

    suspend fun deleteNestingResultsByProject(projectId: String)
}