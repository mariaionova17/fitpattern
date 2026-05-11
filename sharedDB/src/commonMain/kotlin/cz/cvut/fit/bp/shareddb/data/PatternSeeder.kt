package cz.cvut.fit.bp.shareddb.data

import cz.cvut.fit.bp.shareddb.data.dao.PatternDao
import cz.cvut.fit.bp.shareddb.data.entity.PatternEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternVariantEntity
import kotlin.String

object PatternSeeder {

    suspend fun insertSamplePatterns(patternDao: PatternDao) {
        val patterns =  listOf(
            PatternEntity(
                id = "tshirt",
                name = "Men's T-Shirt",
                pictureUri = "tshirt_men.png",
                downloadLink = "https://drive.google.com/file/d/1uTFtXWit-M4xArN5v9grpmPD8_rO1W_v/view?usp=sharing"
            ),
            PatternEntity(
                id = "pleated_miniskirt",
                name = "Pleated Miniskirt",
                pictureUri = "pleated_miniskirt.png",
                downloadLink = "https://drive.google.com/file/d/1yvBVD3sY0fBxc3EgNp_ZMRxth0Y5buAA/view?usp=sharing"
            ),
            PatternEntity(
                id = "cargo_shorts",
                name = "Cargo shorts",
                pictureUri = "cargo_shorts.png",
                downloadLink = "https://drive.google.com/file/d/18kHRUKZ8rarYBLK-IN9_Dazx32FGI64D/view?usp=sharing"
            )
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
            ),
            PatternVariantEntity(
                variantId = "pleated_miniskirt_s",
                patternId = "pleated_miniskirt",
                size = "S"
            ),
            PatternVariantEntity(
                variantId = "pleated_miniskirt_m",
                patternId = "pleated_miniskirt",
                size = "M"
            ),
            PatternVariantEntity(
                variantId = "pleated_miniskirt_l",
                patternId = "pleated_miniskirt",
                size = "L"
            ),
            PatternVariantEntity(
                variantId = "cargo_shorts_s",
                patternId = "cargo_shorts",
                size = "S"
            ),
            PatternVariantEntity(
                variantId = "cargo_shorts_m",
                patternId = "cargo_shorts",
                size = "M"
            ),
            PatternVariantEntity(
                variantId = "cargo_shorts_l",
                patternId = "cargo_shorts",
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
            ),


            //// SKIRT L /////////////////
            PatternPieceEntity(
                id = "pleated_miniskirt_l_skirt_1",
                name = "Skirt 1",
                patternVariantId = "pleated_miniskirt_l",
                contourJson = """
            {
                "points": [
                  {"x": 1084.0, "y": 0.0},
                  {"x": 1084.0, "y": 437.0},
                  {"x": 981.0, "y": 443.0},
                  {"x": 942.0, "y": 440.0},
                  {"x": 903.0, "y": 443.0},
                  {"x": 809.0, "y": 437.0},
                  {"x": 717.0, "y": 442.0},
                  {"x": 676.0, "y": 439.0},
                  {"x": 635.0, "y": 442.0},
                  {"x": 542.0, "y": 438.0},
                  {"x": 449.0, "y": 442.0},
                  {"x": 408.0, "y": 439.0},
                  {"x": 367.0, "y": 442.0},
                  {"x": 275.0, "y": 437.0},
                  {"x": 181.0, "y": 443.0},
                  {"x": 142.0, "y": 440.0},
                  {"x": 103.0, "y": 443.0},
                  {"x": 0.0, "y": 437.0},
                  {"x": 0.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pleated_miniskirt_l_skirt_2",
                name = "Skirt 2",
                patternVariantId = "pleated_miniskirt_l",
                contourJson = """
            {
                "points": [
                  {"x": 1084.0, "y": 0.0},
                  {"x": 1084.0, "y": 437.0},
                  {"x": 981.0, "y": 443.0},
                  {"x": 942.0, "y": 440.0},
                  {"x": 903.0, "y": 443.0},
                  {"x": 809.0, "y": 437.0},
                  {"x": 717.0, "y": 442.0},
                  {"x": 676.0, "y": 439.0},
                  {"x": 635.0, "y": 442.0},
                  {"x": 542.0, "y": 438.0},
                  {"x": 449.0, "y": 442.0},
                  {"x": 408.0, "y": 439.0},
                  {"x": 367.0, "y": 442.0},
                  {"x": 275.0, "y": 437.0},
                  {"x": 181.0, "y": 443.0},
                  {"x": 142.0, "y": 440.0},
                  {"x": 103.0, "y": 443.0},
                  {"x": 0.0, "y": 437.0},
                  {"x": 0.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),

            PatternPieceEntity(
                id = "pleated_miniskirt_l_waist_1",
                name = "Waist 1",
                patternVariantId = "pleated_miniskirt_l",
                contourJson = """
            {
                "points": [
                  {"x": 249.0, "y": 0.0},
                  {"x": 349.0, "y": 2.0},
                  {"x": 419.0, "y": 14.0},
                  {"x": 498.0, "y": 35.0},
                  {"x": 479.0, "y": 93.0},
                  {"x": 343.0, "y": 62.0},
                  {"x": 249.0, "y": 60.0},
                  {"x": 155.0, "y": 62.0},
                  {"x": 19.0, "y": 93.0},
                  {"x": 0.0, "y": 35.0},
                  {"x": 79.0, "y": 14.0},
                  {"x": 149.0, "y": 2.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),

            PatternPieceEntity(
                id = "pleated_miniskirt_l_waist_2",
                name = "Waist 2",
                patternVariantId = "pleated_miniskirt_l",
                contourJson = """
            {
                "points": [
                  {"x": 249.0, "y": 0.0},
                  {"x": 349.0, "y": 2.0},
                  {"x": 419.0, "y": 14.0},
                  {"x": 498.0, "y": 35.0},
                  {"x": 479.0, "y": 93.0},
                  {"x": 343.0, "y": 62.0},
                  {"x": 249.0, "y": 60.0},
                  {"x": 155.0, "y": 62.0},
                  {"x": 19.0, "y": 93.0},
                  {"x": 0.0, "y": 35.0},
                  {"x": 79.0, "y": 14.0},
                  {"x": 149.0, "y": 2.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pleated_miniskirt_l_waist_3",
                name = "Waist 3",
                patternVariantId = "pleated_miniskirt_l",
                contourJson = """
            {
                "points": [
                  {"x": 249.0, "y": 0.0},
                  {"x": 349.0, "y": 2.0},
                  {"x": 419.0, "y": 14.0},
                  {"x": 498.0, "y": 35.0},
                  {"x": 479.0, "y": 93.0},
                  {"x": 343.0, "y": 62.0},
                  {"x": 249.0, "y": 60.0},
                  {"x": 155.0, "y": 62.0},
                  {"x": 19.0, "y": 93.0},
                  {"x": 0.0, "y": 35.0},
                  {"x": 79.0, "y": 14.0},
                  {"x": 149.0, "y": 2.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pleated_miniskirt_l_waist_4",
                name = "Waist 4",
                patternVariantId = "pleated_miniskirt_l",
                contourJson = """
            {
                "points": [
                  {"x": 249.0, "y": 0.0},
                  {"x": 349.0, "y": 2.0},
                  {"x": 419.0, "y": 14.0},
                  {"x": 498.0, "y": 35.0},
                  {"x": 479.0, "y": 93.0},
                  {"x": 343.0, "y": 62.0},
                  {"x": 249.0, "y": 60.0},
                  {"x": 155.0, "y": 62.0},
                  {"x": 19.0, "y": 93.0},
                  {"x": 0.0, "y": 35.0},
                  {"x": 79.0, "y": 14.0},
                  {"x": 149.0, "y": 2.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),

            ////// SKIRT M ///////////////////

            PatternPieceEntity(
                id = "pleated_miniskirt_m_skirt_1",
                name = "Skirt 1",
                patternVariantId = "pleated_miniskirt_m",
                contourJson = """
            {
                "points": [
                  {"x": 1046.0, "y": 0.0},
                  {"x": 1046.0, "y": 437.0},
                  {"x": 948.0, "y": 437.0},
                  {"x": 883.0, "y": 442.0},
                  {"x": 843.0, "y": 440.0},
                  {"x": 755.0, "y": 446.0},
                  {"x": 717.0, "y": 444.0},
                  {"x": 677.0, "y": 447.0},
                  {"x": 616.0, "y": 442.0},
                  {"x": 523.0, "y": 437.0},
                  {"x": 430.0, "y": 442.0},
                  {"x": 369.0, "y": 447.0},
                  {"x": 329.0, "y": 444.0},
                  {"x": 291.0, "y": 446.0},
                  {"x": 203.0, "y": 440.0},
                  {"x": 163.0, "y": 442.0},
                  {"x": 98.0, "y": 437.0},
                  {"x": 0.0, "y": 437.0},
                  {"x": 0.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pleated_miniskirt_m_skirt_2",
                name = "Skirt 2",
                patternVariantId = "pleated_miniskirt_m",
                contourJson = """
            {
                "points": [
                  {"x": 1046.0, "y": 0.0},
                  {"x": 1046.0, "y": 437.0},
                  {"x": 948.0, "y": 437.0},
                  {"x": 883.0, "y": 442.0},
                  {"x": 843.0, "y": 440.0},
                  {"x": 755.0, "y": 446.0},
                  {"x": 717.0, "y": 444.0},
                  {"x": 677.0, "y": 447.0},
                  {"x": 616.0, "y": 442.0},
                  {"x": 523.0, "y": 437.0},
                  {"x": 430.0, "y": 442.0},
                  {"x": 369.0, "y": 447.0},
                  {"x": 329.0, "y": 444.0},
                  {"x": 291.0, "y": 446.0},
                  {"x": 203.0, "y": 440.0},
                  {"x": 163.0, "y": 442.0},
                  {"x": 98.0, "y": 437.0},
                  {"x": 0.0, "y": 437.0},
                  {"x": 0.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),

            PatternPieceEntity(
                id = "pleated_miniskirt_m_waist_1",
                name = "Waist 1",
                patternVariantId = "pleated_miniskirt_m",
                contourJson = """
            {
                "points": [
                  {"x": 228.0, "y": 0.0},
                  {"x": 328.0, "y": 3.0},
                  {"x": 388.0, "y": 12.0},
                  {"x": 456.0, "y": 28.0},
                  {"x": 439.0, "y": 87.0},
                  {"x": 322.0, "y": 63.0},
                  {"x": 228.0, "y": 61.0},
                  {"x": 134.0, "y": 63.0},
                  {"x": 17.0, "y": 87.0},
                  {"x": 0.0, "y": 28.0},
                  {"x": 68.0, "y": 12.0},
                  {"x": 128.0, "y": 3.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pleated_miniskirt_m_waist_2",
                name = "Waist 2",
                patternVariantId = "pleated_miniskirt_m",
                contourJson = """
            {
                "points": [
                  {"x": 228.0, "y": 0.0},
                  {"x": 328.0, "y": 3.0},
                  {"x": 388.0, "y": 12.0},
                  {"x": 456.0, "y": 28.0},
                  {"x": 439.0, "y": 87.0},
                  {"x": 322.0, "y": 63.0},
                  {"x": 228.0, "y": 61.0},
                  {"x": 134.0, "y": 63.0},
                  {"x": 17.0, "y": 87.0},
                  {"x": 0.0, "y": 28.0},
                  {"x": 68.0, "y": 12.0},
                  {"x": 128.0, "y": 3.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pleated_miniskirt_m_waist_3",
                name = "Waist 3",
                patternVariantId = "pleated_miniskirt_m",
                contourJson = """
            {
                "points": [
                  {"x": 228.0, "y": 0.0},
                  {"x": 328.0, "y": 3.0},
                  {"x": 388.0, "y": 12.0},
                  {"x": 456.0, "y": 28.0},
                  {"x": 439.0, "y": 87.0},
                  {"x": 322.0, "y": 63.0},
                  {"x": 228.0, "y": 61.0},
                  {"x": 134.0, "y": 63.0},
                  {"x": 17.0, "y": 87.0},
                  {"x": 0.0, "y": 28.0},
                  {"x": 68.0, "y": 12.0},
                  {"x": 128.0, "y": 3.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pleated_miniskirt_m_waist_4",
                name = "Waist 4",
                patternVariantId = "pleated_miniskirt_m",
                contourJson = """
            {
                "points": [
                  {"x": 228.0, "y": 0.0},
                  {"x": 328.0, "y": 3.0},
                  {"x": 388.0, "y": 12.0},
                  {"x": 456.0, "y": 28.0},
                  {"x": 439.0, "y": 87.0},
                  {"x": 322.0, "y": 63.0},
                  {"x": 228.0, "y": 61.0},
                  {"x": 134.0, "y": 63.0},
                  {"x": 17.0, "y": 87.0},
                  {"x": 0.0, "y": 28.0},
                  {"x": 68.0, "y": 12.0},
                  {"x": 128.0, "y": 3.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),

///////////////////// SKIRT S /////////////////
            PatternPieceEntity(
                id = "pleated_miniskirt_s_skirt_1",
                name = "Skirt 1",
                patternVariantId = "pleated_miniskirt_s",
                contourJson = """
            {
                 "points": [
                  {"x": 1006.0, "y": 0.0},
                  {"x": 1006.0, "y": 436.0},
                  {"x": 914.0, "y": 442.0},
                  {"x": 873.0, "y": 440.0},
                  {"x": 834.0, "y": 442.0},
                  {"x": 738.0, "y": 437.0},
                  {"x": 664.0, "y": 441.0},
                  {"x": 627.0, "y": 438.0},
                  {"x": 588.0, "y": 441.0},
                  {"x": 503.0, "y": 446.0},
                  {"x": 418.0, "y": 441.0},
                  {"x": 379.0, "y": 438.0},
                  {"x": 342.0, "y": 441.0},
                  {"x": 268.0, "y": 437.0},
                  {"x": 172.0, "y": 442.0},
                  {"x": 133.0, "y": 440.0},
                  {"x": 92.0, "y": 442.0},
                  {"x": 0.0, "y": 436.0},
                  {"x": 0.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pleated_miniskirt_s_skirt_2",
                name = "Skirt 2",
                patternVariantId = "pleated_miniskirt_s",
                contourJson = """
            {
                 "points": [
                  {"x": 1006.0, "y": 0.0},
                  {"x": 1006.0, "y": 436.0},
                  {"x": 914.0, "y": 442.0},
                  {"x": 873.0, "y": 440.0},
                  {"x": 834.0, "y": 442.0},
                  {"x": 738.0, "y": 437.0},
                  {"x": 664.0, "y": 441.0},
                  {"x": 627.0, "y": 438.0},
                  {"x": 588.0, "y": 441.0},
                  {"x": 503.0, "y": 446.0},
                  {"x": 418.0, "y": 441.0},
                  {"x": 379.0, "y": 438.0},
                  {"x": 342.0, "y": 441.0},
                  {"x": 268.0, "y": 437.0},
                  {"x": 172.0, "y": 442.0},
                  {"x": 133.0, "y": 440.0},
                  {"x": 92.0, "y": 442.0},
                  {"x": 0.0, "y": 436.0},
                  {"x": 0.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),

            PatternPieceEntity(
                id = "pleated_miniskirt_s_waist_1",
                name = "Waist 1",
                patternVariantId = "pleated_miniskirt_s",
                contourJson = """
            {
                "points": [
                  {"x": 201.0, "y": 0.0},
                  {"x": 301.0, "y": 4.0},
                  {"x": 402.0, "y": 26.0},
                  {"x": 383.0, "y": 81.0},
                  {"x": 295.0, "y": 62.0},
                  {"x": 201.0, "y": 59.0},
                  {"x": 107.0, "y": 62.0},
                  {"x": 19.0, "y": 81.0},
                  {"x": 0.0, "y": 26.0},
                  {"x": 101.0, "y": 4.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pleated_miniskirt_s_waist_2",
                name = "Waist 2",
                patternVariantId = "pleated_miniskirt_s",
                contourJson = """
            {
                "points": [
                  {"x": 201.0, "y": 0.0},
                  {"x": 301.0, "y": 4.0},
                  {"x": 402.0, "y": 26.0},
                  {"x": 383.0, "y": 81.0},
                  {"x": 295.0, "y": 62.0},
                  {"x": 201.0, "y": 59.0},
                  {"x": 107.0, "y": 62.0},
                  {"x": 19.0, "y": 81.0},
                  {"x": 0.0, "y": 26.0},
                  {"x": 101.0, "y": 4.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pleated_miniskirt_s_waist_3",
                name = "Waist 3",
                patternVariantId = "pleated_miniskirt_s",
                contourJson = """
            {
                "points": [
                  {"x": 201.0, "y": 0.0},
                  {"x": 301.0, "y": 4.0},
                  {"x": 402.0, "y": 26.0},
                  {"x": 383.0, "y": 81.0},
                  {"x": 295.0, "y": 62.0},
                  {"x": 201.0, "y": 59.0},
                  {"x": 107.0, "y": 62.0},
                  {"x": 19.0, "y": 81.0},
                  {"x": 0.0, "y": 26.0},
                  {"x": 101.0, "y": 4.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "pleated_miniskirt_s_waist_4",
                name = "Waist 4",
                patternVariantId = "pleated_miniskirt_s",
                contourJson = """
            {
                "points": [
                  {"x": 201.0, "y": 0.0},
                  {"x": 301.0, "y": 4.0},
                  {"x": 402.0, "y": 26.0},
                  {"x": 383.0, "y": 81.0},
                  {"x": 295.0, "y": 62.0},
                  {"x": 201.0, "y": 59.0},
                  {"x": 107.0, "y": 62.0},
                  {"x": 19.0, "y": 81.0},
                  {"x": 0.0, "y": 26.0},
                  {"x": 101.0, "y": 4.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            //////////////////////////CARGO SHORTS L ///////////////////////////
            PatternPieceEntity(
                id = "cargo_shorts_l_back_pocket_1",
                name = "Back Pocket 1",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 12.0, "y": 28.0},
                  {"x": 102.0, "y": 0.0},
                  {"x": 193.0, "y": 28.0},
                  {"x": 206.0, "y": 208.0},
                  {"x": 0.0, "y": 208.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_back_pocket_2",
                name = "Back Pocket 2",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 12.0, "y": 28.0},
                  {"x": 102.0, "y": 0.0},
                  {"x": 193.0, "y": 28.0},
                  {"x": 206.0, "y": 208.0},
                  {"x": 0.0, "y": 208.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_flap_1",
                name = "Flap 1",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 224.0, "y": 0.0},
                  {"x": 224.0, "y": 121.0},
                  {"x": 0.0, "y": 121.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_flap_2",
                name = "Flap 2",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 224.0, "y": 0.0},
                  {"x": 224.0, "y": 121.0},
                  {"x": 0.0, "y": 121.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_pocket_backing_1",
                name = "Pocket backing 1",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 20.0, "y": 0.0},
                  {"x": 129.0, "y": 14.0},
                  {"x": 35.0, "y": 183.0},
                  {"x": 0.0, "y": 183.0},
                  {"x": 2.0, "y": 110.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_pocket_backing_2",
                name = "Pocket backing 2",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 127.0, "y": 110.0},
                  {"x": 129.0, "y": 183.0},
                  {"x": 94.0, "y": 183.0},
                  {"x": 0.0, "y": 14.0},
                  {"x": 109.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_belt_loop",
                name = "Belt loop",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 32.0, "y": 0.0},
                  {"x": 32.0, "y": 481.0},
                  {"x": 0.0, "y": 481.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_fly_shield",
                name = "Fly shield",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 72.0, "y": 0.0},
                  {"x": 72.0, "y": 208.0},
                  {"x": 0.0, "y": 208.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_fly_shield_interfacing",
                name = "Fly shield (interfacing)",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 72.0, "y": 0.0},
                  {"x": 72.0, "y": 208.0},
                  {"x": 0.0, "y": 208.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_side_pocket_1",
                name = "Side pocket 1",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 275.0, "y": 0.0},
                  {"x": 275.0, "y": 220.0},
                  {"x": 0.0, "y": 220.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_side_pocket_2",
                name = "Side pocket 2",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 275.0, "y": 0.0},
                  {"x": 275.0, "y": 220.0},
                  {"x": 0.0, "y": 220.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_waist_band",
                name = "Waist band",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 968.0, "y": 0.0},
                  {"x": 968.0, "y": 92.0},
                  {"x": 0.0, "y": 92.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_waist_band_interfacing",
                name = "Waist band (interfacing)",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 968.0, "y": 0.0},
                  {"x": 968.0, "y": 92.0},
                  {"x": 0.0, "y": 92.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_fly_facing",
                name = "Fly facing",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 30.0, "y": 5.0},
                  {"x": 44.0, "y": 15.0},
                  {"x": 53.0, "y": 50.0},
                  {"x": 53.0, "y": 207.0},
                  {"x": 4.0, "y": 199.0},
                  {"x": 4.0, "y": 30.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_fly_facing_interfacing",
                name = "Fly facing (interfacing)",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 49.0, "y": 30.0},
                  {"x": 49.0, "y": 199.0},
                  {"x": 0.0, "y": 207.0},
                  {"x": 0.0, "y": 50.0},
                  {"x": 9.0, "y": 15.0},
                  {"x": 23.0, "y": 5.0},
                  {"x": 53.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_pocket_lining_1",
                name = "Pocket lining 1",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 16.0, "y": 0.0},
                  {"x": 150.0, "y": 14.0},
                  {"x": 210.0, "y": 11.0},
                  {"x": 277.0, "y": 0.0},
                  {"x": 350.0, "y": 119.0},
                  {"x": 355.0, "y": 196.0},
                  {"x": 337.0, "y": 198.0},
                  {"x": 295.0, "y": 217.0},
                  {"x": 269.0, "y": 225.0},
                  {"x": 87.0, "y": 229.0},
                  {"x": 60.0, "y": 223.0},
                  {"x": 20.0, "y": 208.0},
                  {"x": 2.0, "y": 207.0},
                  {"x": 0.0, "y": 150.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_pocket_lining_2",
                name = "Pocket lining 2",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 355.0, "y": 150.0},
                  {"x": 353.0, "y": 207.0},
                  {"x": 335.0, "y": 208.0},
                  {"x": 295.0, "y": 223.0},
                  {"x": 268.0, "y": 229.0},
                  {"x": 86.0, "y": 225.0},
                  {"x": 60.0, "y": 217.0},
                  {"x": 18.0, "y": 198.0},
                  {"x": 0.0, "y": 196.0},
                  {"x": 5.0, "y": 119.0},
                  {"x": 78.0, "y": 0.0},
                  {"x": 145.0, "y": 11.0},
                  {"x": 205.0, "y": 14.0},
                  {"x": 339.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_front_1",
                name = "Front 1",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 333.0, "y": 0.0},
                  {"x": 331.0, "y": 31.0},
                  {"x": 367.0, "y": 247.0},
                  {"x": 325.0, "y": 274.0},
                  {"x": 278.0, "y": 485.0},
                  {"x": 200.0, "y": 486.0},
                  {"x": 72.0, "y": 498.0},
                  {"x": 2.0, "y": 376.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_front_2",
                name = "Front 2",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 365.0, "y": 376.0},
                  {"x": 295.0, "y": 498.0},
                  {"x": 167.0, "y": 486.0},
                  {"x": 89.0, "y": 485.0},
                  {"x": 42.0, "y": 274.0},
                  {"x": 0.0, "y": 247.0},
                  {"x": 36.0, "y": 31.0},
                  {"x": 34.0, "y": 0.0},
                  {"x": 367.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_back_1",
                name = "Back 1",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 64.0, "y": 0.0},
                  {"x": 453.0, "y": 0.0},
                  {"x": 450.0, "y": 502.0},
                  {"x": 390.0, "y": 509.0},
                  {"x": 320.0, "y": 532.0},
                  {"x": 290.0, "y": 536.0},
                  {"x": 198.0, "y": 561.0},
                  {"x": 103.0, "y": 320.0},
                  {"x": 75.0, "y": 278.0},
                  {"x": 50.0, "y": 260.0},
                  {"x": 0.0, "y": 241.0},
                  {"x": 67.0, "y": 34.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_l_back_2",
                name = "Back 2",
                patternVariantId = "cargo_shorts_l",
                contourJson = """
            {
                "points": [
                  {"x": 386.0, "y": 34.0},
                  {"x": 453.0, "y": 241.0},
                  {"x": 403.0, "y": 260.0},
                  {"x": 378.0, "y": 278.0},
                  {"x": 350.0, "y": 320.0},
                  {"x": 255.0, "y": 561.0},
                  {"x": 163.0, "y": 536.0},
                  {"x": 133.0, "y": 532.0},
                  {"x": 63.0, "y": 509.0},
                  {"x": 3.0, "y": 502.0},
                  {"x": 0.0, "y": 0.0},
                  {"x": 389.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),


            //////////////////////////CARGO SHORTS M ///////////////////////////
            PatternPieceEntity(
                id = "cargo_shorts_m_back_pocket_1",
                name = "Back Pocket 1",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 13.0, "y": 27.0},
                  {"x": 96.0, "y": 0.0},
                  {"x": 181.0, "y": 27.0},
                  {"x": 194.0, "y": 202.0},
                  {"x": 0.0, "y": 202.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_back_pocket_2",
                name = "Back Pocket 2",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 13.0, "y": 27.0},
                  {"x": 96.0, "y": 0.0},
                  {"x": 181.0, "y": 27.0},
                  {"x": 194.0, "y": 202.0},
                  {"x": 0.0, "y": 202.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_flap_1",
                name = "Flap 1",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 211.0, "y": 0.0},
                  {"x": 211.0, "y": 121.0},
                  {"x": 0.0, "y": 121.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_flap_2",
                name = "Flap 2",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 211.0, "y": 0.0},
                  {"x": 211.0, "y": 121.0},
                  {"x": 0.0, "y": 121.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_pocket_backing_1",
                name = "Pocket backing 1",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 20.0, "y": 0.0},
                  {"x": 129.0, "y": 14.0},
                  {"x": 35.0, "y": 183.0},
                  {"x": 0.0, "y": 183.0},
                  {"x": 2.0, "y": 110.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_pocket_backing_2",
                name = "Pocket backing 2",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 127.0, "y": 110.0},
                  {"x": 129.0, "y": 183.0},
                  {"x": 94.0, "y": 183.0},
                  {"x": 0.0, "y": 14.0},
                  {"x": 109.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_belt_loop",
                name = "Belt loop",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 32.0, "y": 0.0},
                  {"x": 32.0, "y": 481.0},
                  {"x": 0.0, "y": 481.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_fly_shield",
                name = "Fly shield",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 72.0, "y": 0.0},
                  {"x": 72.0, "y": 208.0},
                  {"x": 0.0, "y": 208.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_fly_shield_interfacing",
                name = "Fly shield (interfacing)",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 72.0, "y": 0.0},
                  {"x": 72.0, "y": 208.0},
                  {"x": 0.0, "y": 208.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_side_pocket_1",
                name = "Side pocket 1",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 263.0, "y": 0.0},
                  {"x": 263.0, "y": 216.0},
                  {"x": 0.0, "y": 216.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_side_pocket_2",
                name = "Side pocket 2",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 263.0, "y": 0.0},
                  {"x": 263.0, "y": 216.0},
                  {"x": 0.0, "y": 216.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_waist_band",
                name = "Waist band",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 888.0, "y": 0.0},
                  {"x": 888.0, "y": 92.0},
                  {"x": 0.0, "y": 92.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_waist_band_interfacing",
                name = "Waist band (interfacing)",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 888.0, "y": 0.0},
                  {"x": 888.0, "y": 92.0},
                  {"x": 0.0, "y": 92.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_fly_facing",
                name = "Fly facing",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 30.0, "y": 5.0},
                  {"x": 44.0, "y": 15.0},
                  {"x": 53.0, "y": 50.0},
                  {"x": 53.0, "y": 207.0},
                  {"x": 4.0, "y": 199.0},
                  {"x": 4.0, "y": 30.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_fly_facing_interfacing",
                name = "Fly facing (interfacing)",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 49.0, "y": 30.0},
                  {"x": 49.0, "y": 199.0},
                  {"x": 0.0, "y": 207.0},
                  {"x": 0.0, "y": 50.0},
                  {"x": 9.0, "y": 15.0},
                  {"x": 23.0, "y": 5.0},
                  {"x": 53.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_pocket_lining_1",
                name = "Pocket lining 1",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 16.0, "y": 0.0},
                  {"x": 150.0, "y": 14.0},
                  {"x": 210.0, "y": 11.0},
                  {"x": 277.0, "y": 0.0},
                  {"x": 350.0, "y": 119.0},
                  {"x": 355.0, "y": 196.0},
                  {"x": 337.0, "y": 198.0},
                  {"x": 295.0, "y": 217.0},
                  {"x": 269.0, "y": 225.0},
                  {"x": 87.0, "y": 229.0},
                  {"x": 60.0, "y": 223.0},
                  {"x": 20.0, "y": 208.0},
                  {"x": 2.0, "y": 207.0},
                  {"x": 0.0, "y": 150.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_pocket_lining_2",
                name = "Pocket lining 2",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 355.0, "y": 150.0},
                  {"x": 353.0, "y": 207.0},
                  {"x": 335.0, "y": 208.0},
                  {"x": 295.0, "y": 223.0},
                  {"x": 268.0, "y": 229.0},
                  {"x": 86.0, "y": 225.0},
                  {"x": 60.0, "y": 217.0},
                  {"x": 18.0, "y": 198.0},
                  {"x": 0.0, "y": 196.0},
                  {"x": 5.0, "y": 119.0},
                  {"x": 78.0, "y": 0.0},
                  {"x": 145.0, "y": 11.0},
                  {"x": 205.0, "y": 14.0},
                  {"x": 339.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_front_1",
                name = "Front 1",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 2.0, "y": 0.0},
                  {"x": 311.0, "y": 0.0},
                  {"x": 300.0, "y": 29.0},
                  {"x": 343.0, "y": 248.0},
                  {"x": 309.0, "y": 265.0},
                  {"x": 257.0, "y": 476.0},
                  {"x": 190.0, "y": 476.0},
                  {"x": 72.0, "y": 488.0},
                  {"x": 2.0, "y": 365.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_front_2",
                name = "Front 2",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 341.0, "y": 365.0},
                  {"x": 271.0, "y": 488.0},
                  {"x": 153.0, "y": 476.0},
                  {"x": 86.0, "y": 476.0},
                  {"x": 34.0, "y": 265.0},
                  {"x": 0.0, "y": 248.0},
                  {"x": 43.0, "y": 29.0},
                  {"x": 32.0, "y": 0.0},
                  {"x": 341.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_back_1",
                name = "Back 1",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 64.0, "y": 0.0},
                  {"x": 429.0, "y": 0.0},
                  {"x": 424.0, "y": 493.0},
                  {"x": 390.0, "y": 450.0},
                  {"x": 305.0, "y": 521.0},
                  {"x": 192.0, "y": 548.0},
                  {"x": 103.0, "y": 320.0},
                  {"x": 57.0, "y": 262.0},
                  {"x": 0.0, "y": 240.0},
                  {"x": 67.0, "y": 30.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_m_back_2",
                name = "Back 2",
                patternVariantId = "cargo_shorts_m",
                contourJson = """
            {
                "points": [
                  {"x": 362.0, "y": 30.0},
                  {"x": 429.0, "y": 240.0},
                  {"x": 372.0, "y": 262.0},
                  {"x": 326.0, "y": 320.0},
                  {"x": 237.0, "y": 548.0},
                  {"x": 124.0, "y": 521.0},
                  {"x": 39.0, "y": 450.0},
                  {"x": 5.0, "y": 493.0},
                  {"x": 0.0, "y": 0.0},
                  {"x": 365.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),

            //////////////////////////CARGO SHORTS S ///////////////////////////
            PatternPieceEntity(
                id = "cargo_shorts_s_back_pocket_1",
                name = "Back Pocket 1",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 13.0, "y": 24.0},
                  {"x": 90.0, "y": 0.0},
                  {"x": 168.0, "y": 24.0},
                  {"x": 182.0, "y": 196.0},
                  {"x": 0.0, "y": 196.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_back_pocket_2",
                name = "Back Pocket 2",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 13.0, "y": 24.0},
                  {"x": 90.0, "y": 0.0},
                  {"x": 168.0, "y": 24.0},
                  {"x": 182.0, "y": 196.0},
                  {"x": 0.0, "y": 196.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_flap_1",
                name = "Flap 1",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 199.0, "y": 0.0},
                  {"x": 199.0, "y": 121.0},
                  {"x": 0.0, "y": 121.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_flap_2",
                name = "Flap 2",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 199.0, "y": 0.0},
                  {"x": 199.0, "y": 121.0},
                  {"x": 0.0, "y": 121.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_pocket_backing_1",
                name = "Pocket backing 1",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 20.0, "y": 0.0},
                  {"x": 129.0, "y": 14.0},
                  {"x": 35.0, "y": 183.0},
                  {"x": 0.0, "y": 183.0},
                  {"x": 2.0, "y": 110.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_pocket_backing_2",
                name = "Pocket backing 2",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 127.0, "y": 110.0},
                  {"x": 129.0, "y": 183.0},
                  {"x": 94.0, "y": 183.0},
                  {"x": 0.0, "y": 14.0},
                  {"x": 109.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_belt_loop",
                name = "Belt loop",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 32.0, "y": 0.0},
                  {"x": 32.0, "y": 481.0},
                  {"x": 0.0, "y": 481.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_fly_shield",
                name = "Fly shield",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 72.0, "y": 0.0},
                  {"x": 72.0, "y": 208.0},
                  {"x": 0.0, "y": 208.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_fly_shield_interfacing",
                name = "Fly shield (interfacing)",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 72.0, "y": 0.0},
                  {"x": 72.0, "y": 208.0},
                  {"x": 0.0, "y": 208.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_side_pocket_1",
                name = "Side pocket 1",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 251.0, "y": 0.0},
                  {"x": 251.0, "y": 212.0},
                  {"x": 0.0, "y": 212.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_side_pocket_2",
                name = "Side pocket 2",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 251.0, "y": 0.0},
                  {"x": 251.0, "y": 212.0},
                  {"x": 0.0, "y": 212.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_waist_band",
                name = "Waist band",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 807.0, "y": 0.0},
                  {"x": 807.0, "y": 92.0},
                  {"x": 0.0, "y": 92.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_waist_band_interfacing",
                name = "Waist band (interfacing)",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 807.0, "y": 0.0},
                  {"x": 807.0, "y": 92.0},
                  {"x": 0.0, "y": 92.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_fly_facing",
                name = "Fly facing",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 30.0, "y": 5.0},
                  {"x": 44.0, "y": 15.0},
                  {"x": 53.0, "y": 50.0},
                  {"x": 53.0, "y": 207.0},
                  {"x": 4.0, "y": 199.0},
                  {"x": 4.0, "y": 30.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_fly_facing_interfacing",
                name = "Fly facing (interfacing)",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 49.0, "y": 30.0},
                  {"x": 49.0, "y": 199.0},
                  {"x": 0.0, "y": 207.0},
                  {"x": 0.0, "y": 50.0},
                  {"x": 9.0, "y": 15.0},
                  {"x": 23.0, "y": 5.0},
                  {"x": 53.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_pocket_lining_1",
                name = "Pocket lining 1",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 16.0, "y": 0.0},
                  {"x": 150.0, "y": 14.0},
                  {"x": 210.0, "y": 11.0},
                  {"x": 277.0, "y": 0.0},
                  {"x": 350.0, "y": 119.0},
                  {"x": 355.0, "y": 196.0},
                  {"x": 337.0, "y": 198.0},
                  {"x": 295.0, "y": 217.0},
                  {"x": 269.0, "y": 225.0},
                  {"x": 87.0, "y": 229.0},
                  {"x": 60.0, "y": 223.0},
                  {"x": 20.0, "y": 208.0},
                  {"x": 2.0, "y": 207.0},
                  {"x": 0.0, "y": 150.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_pocket_lining_2",
                name = "Pocket lining 2",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 355.0, "y": 150.0},
                  {"x": 353.0, "y": 207.0},
                  {"x": 335.0, "y": 208.0},
                  {"x": 295.0, "y": 223.0},
                  {"x": 268.0, "y": 229.0},
                  {"x": 86.0, "y": 225.0},
                  {"x": 60.0, "y": 217.0},
                  {"x": 18.0, "y": 198.0},
                  {"x": 0.0, "y": 196.0},
                  {"x": 5.0, "y": 119.0},
                  {"x": 78.0, "y": 0.0},
                  {"x": 145.0, "y": 11.0},
                  {"x": 205.0, "y": 14.0},
                  {"x": 339.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_front_1",
                name = "Front 1",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 0.0, "y": 0.0},
                  {"x": 286.0, "y": 0.0},
                  {"x": 284.0, "y": 31.0},
                  {"x": 318.0, "y": 248.0},
                  {"x": 280.0, "y": 267.0},
                  {"x": 237.0, "y": 466.0},
                  {"x": 170.0, "y": 466.0},
                  {"x": 71.0, "y": 478.0},
                  {"x": 2.0, "y": 365.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_front_2",
                name = "Front 2",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 316.0, "y": 365.0},
                  {"x": 247.0, "y": 478.0},
                  {"x": 148.0, "y": 466.0},
                  {"x": 81.0, "y": 466.0},
                  {"x": 38.0, "y": 267.0},
                  {"x": 0.0, "y": 248.0},
                  {"x": 34.0, "y": 31.0},
                  {"x": 32.0, "y": 0.0},
                  {"x": 318.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_back_1",
                name = "Back 1",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 63.0, "y": 0.0},
                  {"x": 404.0, "y": 0.0},
                  {"x": 399.0, "y": 481.0},
                  {"x": 350.0, "y": 488.0},
                  {"x": 292.0, "y": 510.0},
                  {"x": 187.0, "y": 535.0},
                  {"x": 91.0, "y": 300.0},
                  {"x": 56.0, "y": 261.0},
                  {"x": 0.0, "y": 240.0},
                  {"x": 65.0, "y": 30.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            ),
            PatternPieceEntity(
                id = "cargo_shorts_s_back_2",
                name = "Back 2",
                patternVariantId = "cargo_shorts_s",
                contourJson = """
            {
                "points": [
                  {"x": 339.0, "y": 30.0},
                  {"x": 404.0, "y": 240.0},
                  {"x": 348.0, "y": 261.0},
                  {"x": 313.0, "y": 300.0},
                  {"x": 217.0, "y": 535.0},
                  {"x": 112.0, "y": 510.0},
                  {"x": 54.0, "y": 488.0},
                  {"x": 5.0, "y": 481.0},
                  {"x": 0.0, "y": 0.0},
                  {"x": 341.0, "y": 0.0}
                ]
            }
        """.trimIndent(),
                holesJson = "[]"
            )
        )

        patternDao.insertPatterns(patterns)
        patternDao.insertVariants(variants)
        patternDao.insertPieces(pieces)
    }
}