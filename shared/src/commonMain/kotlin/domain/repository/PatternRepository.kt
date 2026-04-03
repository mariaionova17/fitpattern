package domain.repository

import domain.pattern.Pattern

interface PatternRepository {
    suspend fun savePattern(pattern: Pattern)
    suspend fun getPatternById(id: String): Pattern?
    suspend fun getAllPatterns(): List<Pattern>
    suspend fun deletePattern(id: String)
}