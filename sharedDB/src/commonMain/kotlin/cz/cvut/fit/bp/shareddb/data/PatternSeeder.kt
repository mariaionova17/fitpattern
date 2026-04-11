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
            // --------------------
            // SIZE S (20 pieces)
            // --------------------
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
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_sleeve_left",
                name = "Sleeve Left",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 20.0},
                  {"x": 55.0, "y": 0.0},
                  {"x": 110.0, "y": 20.0},
                  {"x": 95.0, "y": 85.0},
                  {"x": 20.0, "y": 80.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_sleeve_right",
                name = "Sleeve Right",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 18.0},
                  {"x": 52.0, "y": 0.0},
                  {"x": 108.0, "y": 22.0},
                  {"x": 92.0, "y": 84.0},
                  {"x": 18.0, "y": 78.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_neck_band",
                name = "Neck Band",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 10.0},
                  {"x": 30.0, "y": 0.0},
                  {"x": 70.0, "y": 0.0},
                  {"x": 100.0, "y": 10.0},
                  {"x": 90.0, "y": 28.0},
                  {"x": 12.0, "y": 28.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_pocket",
                name = "Pocket",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 48.0, "y": 0.0},
                  {"x": 52.0, "y": 42.0},
                  {"x": 26.0, "y": 58.0},
                  {"x": 0.0, "y": 42.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_cuff_left",
                name = "Cuff Left",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 8.0},
                  {"x": 18.0, "y": 0.0},
                  {"x": 72.0, "y": 0.0},
                  {"x": 90.0, "y": 8.0},
                  {"x": 84.0, "y": 26.0},
                  {"x": 6.0, "y": 26.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_cuff_right",
                name = "Cuff Right",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 7.0},
                  {"x": 16.0, "y": 0.0},
                  {"x": 70.0, "y": 0.0},
                  {"x": 88.0, "y": 7.0},
                  {"x": 82.0, "y": 25.0},
                  {"x": 5.0, "y": 25.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_hem_front",
                name = "Hem Front",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 5.0},
                  {"x": 22.0, "y": 0.0},
                  {"x": 110.0, "y": 0.0},
                  {"x": 132.0, "y": 5.0},
                  {"x": 126.0, "y": 22.0},
                  {"x": 8.0, "y": 22.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_hem_back",
                name = "Hem Back",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 6.0},
                  {"x": 20.0, "y": 0.0},
                  {"x": 108.0, "y": 0.0},
                  {"x": 128.0, "y": 6.0},
                  {"x": 122.0, "y": 24.0},
                  {"x": 7.0, "y": 24.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_side_panel_left",
                name = "Side Panel Left",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 42.0, "y": 12.0},
                  {"x": 56.0, "y": 82.0},
                  {"x": 34.0, "y": 140.0},
                  {"x": 0.0, "y": 124.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_side_panel_right",
                name = "Side Panel Right",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 10.0},
                  {"x": 40.0, "y": 0.0},
                  {"x": 58.0, "y": 76.0},
                  {"x": 36.0, "y": 142.0},
                  {"x": 2.0, "y": 128.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_yoke",
                name = "Yoke",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 18.0},
                  {"x": 26.0, "y": 0.0},
                  {"x": 92.0, "y": 0.0},
                  {"x": 120.0, "y": 18.0},
                  {"x": 100.0, "y": 54.0},
                  {"x": 18.0, "y": 50.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_collar_piece",
                name = "Collar Piece",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 12.0},
                  {"x": 22.0, "y": 0.0},
                  {"x": 74.0, "y": 0.0},
                  {"x": 96.0, "y": 12.0},
                  {"x": 82.0, "y": 38.0},
                  {"x": 16.0, "y": 36.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_facing_left",
                name = "Facing Left",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 36.0, "y": 8.0},
                  {"x": 46.0, "y": 64.0},
                  {"x": 16.0, "y": 96.0},
                  {"x": 0.0, "y": 84.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_facing_right",
                name = "Facing Right",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 6.0},
                  {"x": 34.0, "y": 0.0},
                  {"x": 48.0, "y": 60.0},
                  {"x": 18.0, "y": 98.0},
                  {"x": 0.0, "y": 86.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_patch_1",
                name = "Patch 1",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 14.0},
                  {"x": 18.0, "y": 0.0},
                  {"x": 42.0, "y": 0.0},
                  {"x": 58.0, "y": 14.0},
                  {"x": 48.0, "y": 38.0},
                  {"x": 12.0, "y": 38.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_patch_2",
                name = "Patch 2",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 40.0, "y": 0.0},
                  {"x": 58.0, "y": 22.0},
                  {"x": 44.0, "y": 50.0},
                  {"x": 14.0, "y": 46.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_insert_left",
                name = "Insert Left",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 12.0},
                  {"x": 26.0, "y": 0.0},
                  {"x": 72.0, "y": 10.0},
                  {"x": 68.0, "y": 54.0},
                  {"x": 18.0, "y": 62.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pattern_1_s_insert_right",
                name = "Insert Right",
                patternVariantId = "pattern_1_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 10.0},
                  {"x": 24.0, "y": 0.0},
                  {"x": 70.0, "y": 12.0},
                  {"x": 66.0, "y": 56.0},
                  {"x": 16.0, "y": 60.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),

            // --------------------
            // SIZE M
            // --------------------
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
                ]
            }
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
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),

            // --------------------
            // SIZE L
            // --------------------
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
                ]
            }
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
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            )
        )

        patternDao.insertPattern(pattern)
        patternDao.insertVariants(variants)
        patternDao.insertPieces(pieces)
    }
}