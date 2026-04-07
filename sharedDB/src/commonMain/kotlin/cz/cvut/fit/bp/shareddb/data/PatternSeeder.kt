package cz.cvut.fit.bp.shareddb.data

import cz.cvut.fit.bp.shareddb.data.dao.PatternDao
import cz.cvut.fit.bp.shareddb.data.entity.PatternEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternVariantEntity
import kotlin.String

object PatternSeeder {

    suspend fun insertSamplePatterns(patternDao: PatternDao) {
        val pattern = PatternEntity(
            id = "pattern_1",
            name = "Basic T-Shirt",
            pictureUri = ""
        )

        val variants = listOf(
            PatternVariantEntity(
                variantId = "pattern_1_s",
                patternId = "pattern_1",
                size = "S"
            ),
            PatternVariantEntity(
                variantId = "pattern_1_m",
                patternId = "pattern_1",
                size = "M"
            ),
            PatternVariantEntity(
                variantId = "pattern_1_l",
                patternId = "pattern_1",
                size = "L"
            )
        )

        val pieces = listOf(
            PatternPieceEntity(
                id = "pattern_1_s_front",
                name = "Front",
                patternVariantId = "pattern_1_s",
                contourJson = """
                    {
                        "points": [
                          {"x": 0.0, "y": 0.0},
                          {"x": 120.0, "y": 0.0},
                          {"x": 60.0, "y": 180.0}
                        ]
                    }
                """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_back",
                name = "Back",
                patternVariantId = "pattern_1_s",
                contourJson = """
                    {
                        "points": [
                      {"x": 0.0, "y": 0.0},
                      {"x": 118.0, "y": 0.0},
                      {"x": 60.0, "y": 178.0}
                    ]}
                """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_m_front",
                name = "Front",
                patternVariantId = "pattern_1_m",
                contourJson = """
                    {
                        "points": [
                      {"x": 0.0, "y": 0.0},
                      {"x": 130.0, "y": 0.0},
                      {"x": 130.0, "y": 190.0},
                      {"x": 0.0, "y": 190.0}
                    ]}
                """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_m_back",
                name = "Back",
                patternVariantId = "pattern_1_m",
                contourJson = """
                    {
                        "points": [
                      {"x": 0.0, "y": 0.0},
                      {"x": 128.0, "y": 0.0},
                      {"x": 128.0, "y": 188.0},
                      {"x": 0.0, "y": 188.0}
                    ]}
                """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_l_front",
                name = "Front",
                patternVariantId = "pattern_1_l",
                contourJson = """
                    {
                        "points": [
                      {"x": 0.0, "y": 0.0},
                      {"x": 140.0, "y": 0.0},
                      {"x": 140.0, "y": 200.0},
                      {"x": 0.0, "y": 200.0}
                    ]}
                """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_l_back",
                name = "Back",
                patternVariantId = "pattern_1_l",
                contourJson = """
                    {
                        "points": [
                      {"x": 0.0, "y": 0.0},
                      {"x": 138.0, "y": 0.0},
                      {"x": 138.0, "y": 198.0},
                      {"x": 0.0, "y": 198.0}
                    ]}
                """.trimIndent(),
                holesJson = "[]"
            )
        )

        patternDao.insertPattern(pattern)
        patternDao.insertVariants(variants)
        patternDao.insertPieces(pieces)
    }
}