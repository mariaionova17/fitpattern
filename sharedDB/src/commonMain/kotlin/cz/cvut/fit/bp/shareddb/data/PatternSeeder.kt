package cz.cvut.fit.bp.shareddb.data

import cz.cvut.fit.bp.shareddb.data.dao.PatternDao
import cz.cvut.fit.bp.shareddb.data.entity.PatternEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternVariantEntity
import kotlin.String

object PatternSeeder {

    suspend fun insertSamplePatterns(patternDao: PatternDao) {
        val pattern = PatternEntity(
            id = "tshirt",
            name = "Men's T-Shirt",
            pictureUri = "tshirt_men.jpg",
            downloadLink = "https://drive.google.com/file/d/1uTFtXWit-M4xArN5v9grpmPD8_rO1W_v/view?usp=sharing"
        )

        val variants = listOf(
            PatternVariantEntity(
                variantId = "tshirt_s",
                patternId = "tshirt",
                size = "S"
            ),
            PatternVariantEntity(
                variantId = "tshirt_m",
                patternId = "tshirt",
                size = "M"
            ),
            PatternVariantEntity(
                variantId = "tshirt_l",
                patternId = "tshirt",
                size = "L"
            )
        )

        val pieces = listOf(
            // --------------------
            // SIZE S (5 pieces)
            // --------------------
            PatternPieceEntity(
                id = "tshirt_s_collar",
                name = "Collar",
                patternVariantId = "tshirt_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 20.0, "y": 0.0},
                  {"x": 20.0, "y": 462.0},
                  {"x": 0.0, "y": 462.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "tshirt_s_sleeve_1",
                name = "Sleeve 1",
                patternVariantId = "tshirt_s",
                contourJson = """
            {
                "points": [
                  {"x": 27.0, "y": 0.0},
                  {"x": 371.0, "y": 0.0},
                  {"x": 398.0, "y": 100.0},
                  {"x": 358.0, "y": 120.0},
                  {"x": 250.0, "y": 230.0},
                  {"x": 210.0, "y": 243.0},
                  {"x": 180.0, "y": 243.0},
                  {"x": 140.0, "y": 230.0},
                  {"x": 39.0, "y": 120.0},
                  {"x": 0.0, "y": 100.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "tshirt_s_sleeve_2",
                name = "Sleeve 2",
                patternVariantId = "tshirt_s",
                contourJson = """
            {
                "points": [
                  {"x": 27.0, "y": 0.0},
                  {"x": 371.0, "y": 0.0},
                  {"x": 398.0, "y": 100.0},
                  {"x": 358.0, "y": 120.0},
                  {"x": 250.0, "y": 230.0},
                  {"x": 210.0, "y": 243.0},
                  {"x": 180.0, "y": 243.0},
                  {"x": 140.0, "y": 230.0},
                  {"x": 39.0, "y": 120.0},
                  {"x": 0.0, "y": 100.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "tshirt_s_back",
                name = "Back",
                patternVariantId = "tshirt_s",
                contourJson = """
            {
                "points": [
                  {"x": 13.0, "y": 0.0},
                  {"x": 521.0, "y": 0.0},
                  {"x": 534.0, "y": 487.0},
                  {"x": 489.0, "y": 520.0},
                  {"x": 502.0, "y": 718.0},
                  {"x": 386.0, "y": 755.0},
                  {"x": 367.0, "y": 732.0},
                  {"x": 267.0, "y": 729.0},
                  {"x": 167.0, "y": 732.0},
                  {"x": 148.0, "y": 755.0},
                  {"x": 32.0, "y": 718.0},
                  {"x": 45.0, "y": 520.0},
                  {"x": 0.0, "y": 487.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "tshirt_s_front",
                name = "Front",
                patternVariantId = "tshirt_s",
                contourJson = """
            {
                "points": [
                  {"x": 17.0, "y": 0.0},
                  {"x": 521.0, "y": 0.0},
                  {"x": 538.0, "y": 487.0},
                  {"x": 482.0, "y": 540.0},
                  {"x": 476.0, "y": 600.0},
                  {"x": 502.0, "y": 707.0},
                  {"x": 391.0, "y": 738.0},
                  {"x": 381.0, "y": 700.0},
                  {"x": 344.0, "y": 656.0},
                  {"x": 269.0, "y": 642.0},
                  {"x": 194.0, "y": 656.0},
                  {"x": 157.0, "y": 700.0},
                  {"x": 147.0, "y": 738.0},
                  {"x": 36.0, "y": 707.0},
                  {"x": 62.0, "y": 600.0},
                  {"x": 56.0, "y": 540.0},
                  {"x": 0.0, "y": 487.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),

            // --------------------
            // SIZE M (5 pieces)
            // --------------------
            PatternPieceEntity(
                id = "tshirt_m_collar",
                name = "Collar",
                patternVariantId = "tshirt_m",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 20.0, "y": 0.0},
                  {"x": 20.0, "y": 481.0},
                  {"x": 0.0, "y": 481.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "tshirt_m_sleeve_1",
                name = "Sleeve 1",
                patternVariantId = "tshirt_m",
                contourJson = """
            {
                "points": [
                  {"x": 37.0, "y": 0.0},
                  {"x": 385.0, "y": 0.0},
                  {"x": 411.0, "y": 103.0},
                  {"x": 361.0, "y": 130.0},
                  {"x": 268.0, "y": 230.0},
                  {"x": 220.0, "y": 250.0},
                  {"x": 170.0, "y": 250.0},
                  {"x": 137.0, "y": 230.0},
                  {"x": 48.0, "y": 130.0},
                  {"x": 0.0, "y": 103.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "tshirt_m_sleeve_2",
                name = "Sleeve 2",
                patternVariantId = "tshirt_m",
                contourJson = """
            {
                "points": [
                  {"x": 37.0, "y": 0.0},
                  {"x": 385.0, "y": 0.0},
                  {"x": 411.0, "y": 103.0},
                  {"x": 361.0, "y": 130.0},
                  {"x": 268.0, "y": 230.0},
                  {"x": 220.0, "y": 250.0},
                  {"x": 170.0, "y": 250.0},
                  {"x": 137.0, "y": 230.0},
                  {"x": 48.0, "y": 130.0},
                  {"x": 0.0, "y": 103.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "tshirt_m_back",
                name = "Back",
                patternVariantId = "tshirt_m",
                contourJson = """
            {
                "points": [
                  {"x": 13.0, "y": 0.0},
                  {"x": 547.0, "y": 0.0},
                  {"x": 560.0, "y": 490.0},
                  {"x": 513.0, "y": 520.0},
                  {"x": 521.0, "y": 726.0},
                  {"x": 402.0, "y": 764.0},
                  {"x": 380.0, "y": 738.0},
                  {"x": 280.0, "y": 736.0},
                  {"x": 180.0, "y": 738.0},
                  {"x": 158.0, "y": 764.0},
                  {"x": 39.0, "y": 726.0},
                  {"x": 47.0, "y": 520.0},
                  {"x": 0.0, "y": 490.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "tshirt_m_front",
                name = "Front",
                patternVariantId = "tshirt_m",
                contourJson = """
            {
                "points": [
                  {"x": 17.0, "y": 0.0},
                  {"x": 549.0, "y": 0.0},
                  {"x": 566.0, "y": 491.0},
                  {"x": 508.0, "y": 540.0},
                  {"x": 499.0, "y": 600.0},
                  {"x": 523.0, "y": 716.0},
                  {"x": 410.0, "y": 750.0},
                  {"x": 393.0, "y": 700.0},
                  {"x": 358.0, "y": 664.0},
                  {"x": 283.0, "y": 649.0},
                  {"x": 208.0, "y": 664.0},
                  {"x": 173.0, "y": 700.0},
                  {"x": 156.0, "y": 750.0},
                  {"x": 43.0, "y": 716.0},
                  {"x": 67.0, "y": 600.0},
                  {"x": 58.0, "y": 540.0},
                  {"x": 0.0, "y": 491.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),

            // --------------------
            // SIZE L (5 pieces)
            // --------------------
            PatternPieceEntity(
                id = "tshirt_l_collar",
                name = "Collar",
                patternVariantId = "tshirt_l",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 20.0, "y": 0.0},
                  {"x": 20.0, "y": 500.0},
                  {"x": 0.0, "y": 500.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "tshirt_l_sleeve_1",
                name = "Sleeve 1",
                patternVariantId = "tshirt_l",
                contourJson = """
            {
                "points": [
                  {"x": 28.0, "y": 0.0},
                  {"x": 399.0, "y": 0.0},
                  {"x": 425.0, "y": 107.0},
                  {"x": 385.0, "y": 125.0},
                  {"x": 295.0, "y": 220.0},
                  {"x": 266.0, "y": 244.0},
                  {"x": 225.0, "y": 256.0},
                  {"x": 185.0, "y": 256.0},
                  {"x": 153.0, "y": 243.0},
                  {"x": 125.0, "y": 220.0},
                  {"x": 47.0, "y": 130.0},
                  {"x": 0.0, "y": 106.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "tshirt_l_sleeve_2",
                name = "Sleeve 2",
                patternVariantId = "tshirt_l",
                contourJson = """
            {
                "points": [
                  {"x": 28.0, "y": 0.0},
                  {"x": 399.0, "y": 0.0},
                  {"x": 425.0, "y": 107.0},
                  {"x": 385.0, "y": 125.0},
                  {"x": 295.0, "y": 220.0},
                  {"x": 266.0, "y": 244.0},
                  {"x": 225.0, "y": 256.0},
                  {"x": 185.0, "y": 256.0},
                  {"x": 153.0, "y": 243.0},
                  {"x": 125.0, "y": 220.0},
                  {"x": 47.0, "y": 130.0},
                  {"x": 0.0, "y": 106.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "tshirt_l_back",
                name = "Back",
                patternVariantId = "tshirt_l",
                contourJson = """
            {
                "points": [
                  {"x": 12.0, "y": 0.0},
                  {"x": 572.0, "y": 0.0},
                  {"x": 584.0, "y": 494.0},
                  {"x": 539.0, "y": 520.0},
                  {"x": 540.0, "y": 735.0},
                  {"x": 416.0, "y": 774.0},
                  {"x": 392.0, "y": 745.0},
                  {"x": 292.0, "y": 742.0},
                  {"x": 192.0, "y": 755.0},
                  {"x": 170.0, "y": 774.0},
                  {"x": 44.0, "y": 735.0},
                  {"x": 45.0, "y": 520.0},
                  {"x": 0.0, "y": 494.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "tshirt_l_front",
                name = "Front",
                patternVariantId = "tshirt_l",
                contourJson = """
            {
                "points": [
                  {"x": 16.0, "y": 0.0},
                  {"x": 574.0, "y": 0.0},
                  {"x": 590.0, "y": 496.0},
                  {"x": 532.0, "y": 540.0},
                  {"x": 520.0, "y": 600.0},
                  {"x": 541.0, "y": 726.0},
                  {"x": 423.0, "y": 761.0},
                  {"x": 401.0, "y": 700.0},
                  {"x": 370.0, "y": 670.0},
                  {"x": 295.0, "y": 656.0},
                  {"x": 220.0, "y": 670.0},
                  {"x": 189.0, "y": 700.0},
                  {"x": 167.0, "y": 761.0},
                  {"x": 49.0, "y": 726.0},
                  {"x": 70.0, "y": 600.0},
                  {"x": 58.0, "y": 540.0},
                  {"x": 0.0, "y": 496.0}
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