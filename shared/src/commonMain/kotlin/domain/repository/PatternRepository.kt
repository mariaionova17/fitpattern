package domain.repository

import domain.pattern.Pattern
import domain.pattern.PatternPiece

interface PatternRepository {
    suspend fun savePattern(pattern: Pattern)
    suspend fun getPatternById(id: String): Pattern?
    suspend fun getPatternByVariantId(variantId: String): List<PatternPiece>
    suspend fun getSizeByVariantId(variantId: String): String?
    suspend fun getAllPatterns(): List<Pattern>
    suspend fun deletePattern(id: String)
}