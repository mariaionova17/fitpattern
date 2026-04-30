package cz.cvut.fit.bp.shareddb.data.repository

import cz.cvut.fit.bp.shareddb.data.entity.PatternEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternVariantEntity
import cz.cvut.fit.bp.shareddb.data.mapper.toPatternPieceEntity
import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.pattern.Pattern
import domain.pattern.PatternPiece
import domain.pattern.PatternVariant
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PatternRepositoryImplTest {

    @Test
    fun `savePattern stores pattern variants and pieces`() = runTest {
        val dao = FakePatternDao()
        val repository = PatternRepositoryImpl(dao)

        val pattern = samplePattern()

        repository.savePattern(pattern)

        assertEquals(
            PatternEntity(
                id = "pattern-1",
                name = "Basic T-Shirt",
                pictureUri = "content://pattern/image",
                downloadLink = "https://example.com/pattern"
            ),
            dao.patterns["pattern-1"]
        )

        assertEquals(2, dao.getVariantsByPatternId("pattern-1").size)
        assertEquals(3, dao.pieces.size)
        assertEquals(
            listOf("insertPattern:pattern-1", "insertVariants:2", "insertPieces:3"),
            dao.callLog
        )
    }

    @Test
    fun `getPatternById returns null when pattern is missing`() = runTest {
        val repository = PatternRepositoryImpl(FakePatternDao())

        val result = repository.getPatternById("missing")

        assertNull(result)
    }

    @Test
    fun `getPatternById reconstructs full pattern with variants and pieces`() = runTest {
        val dao = FakePatternDao()
        val pattern = samplePattern()

        dao.patterns[pattern.id] = PatternEntity(
            id = pattern.id,
            name = pattern.name,
            pictureUri = pattern.pictureUri,
            downloadLink = pattern.downloadLink
        )

        pattern.variants.forEach { variant ->
            dao.variants[variant.id] = PatternVariantEntity(
                variantId = variant.id,
                patternId = pattern.id,
                size = variant.size
            )
            variant.pieces.forEach { piece ->
                dao.pieces[piece.id] = piece.toPatternPieceEntity(variant.id)
            }
        }

        val repository = PatternRepositoryImpl(dao)

        val restored = repository.getPatternById(pattern.id)

        assertEquals(pattern, restored)
    }

    @Test
    fun `getPatternByVariantId returns mapped pieces`() = runTest {
        val dao = FakePatternDao()
        val pieces = listOf(
            patternPiece("piece-1", "Front", 4.0, 3.0),
            patternPiece("piece-2", "Back", 5.0, 3.0)
        )

        pieces.forEach { piece ->
            dao.pieces[piece.id] = piece.toPatternPieceEntity("variant-m")
        }

        val repository = PatternRepositoryImpl(dao)

        val restored = repository.getPatternByVariantId("variant-m")

        assertEquals(pieces, restored)
    }

    @Test
    fun `getSizeByVariantId delegates to dao`() = runTest {
        val dao = FakePatternDao()
        dao.variants["variant-m"] = PatternVariantEntity(
            variantId = "variant-m",
            patternId = "pattern-1",
            size = "M"
        )

        val repository = PatternRepositoryImpl(dao)

        val size = repository.getSizeByVariantId("variant-m")

        assertEquals("M", size)
    }

    @Test
    fun `getAllPatterns reconstructs all patterns`() = runTest {
        val dao = FakePatternDao()

        val patternA = samplePattern()
        val patternB = Pattern(
            id = "pattern-2",
            name = "Skirt",
            pictureUri = "content://pattern/skirt",
            downloadLink = "https://example.com/skirt",
            variants = listOf(
                PatternVariant(
                    id = "pattern-2-variant-s",
                    size = "S",
                    pieces = listOf(patternPiece("piece-10", "Skirt Front", 6.0, 4.0))
                )
            )
        )

        listOf(patternA, patternB).forEach { pattern ->
            dao.patterns[pattern.id] = PatternEntity(
                id = pattern.id,
                name = pattern.name,
                pictureUri = pattern.pictureUri,
                downloadLink = pattern.downloadLink
            )
            pattern.variants.forEach { variant ->
                dao.variants[variant.id] = PatternVariantEntity(
                    variantId = variant.id,
                    patternId = pattern.id,
                    size = variant.size
                )
                variant.pieces.forEach { piece ->
                    dao.pieces[piece.id] = piece.toPatternPieceEntity(variant.id)
                }
            }
        }

        val repository = PatternRepositoryImpl(dao)

        val patterns = repository.getAllPatterns()

        assertEquals(listOf(patternA, patternB), patterns)
    }

    @Test
    fun `deletePattern delegates to dao`() = runTest {
        val dao = FakePatternDao()
        val repository = PatternRepositoryImpl(dao)

        repository.deletePattern("pattern-1")

        assertEquals(listOf("deletePattern:pattern-1"), dao.callLog)
    }

    private fun samplePattern(): Pattern =
        Pattern(
            id = "pattern-1",
            name = "Basic T-Shirt",
            pictureUri = "content://pattern/image",
            downloadLink = "https://example.com/pattern",
            variants = listOf(
                PatternVariant(
                    id = "variant-s",
                    size = "S",
                    pieces = listOf(
                        patternPiece("piece-1", "Front", 4.0, 3.0),
                        patternPiece("piece-2", "Back", 4.5, 3.0)
                    )
                ),
                PatternVariant(
                    id = "variant-m",
                    size = "M",
                    pieces = listOf(
                        patternPiece("piece-3", "Sleeve", 2.0, 2.0)
                    )
                )
            )
        )

    private fun patternPiece(id: String, name: String, width: Double, height: Double): PatternPiece =
        PatternPiece(
            id = id,
            name = name,
            shape = Shape(
                contour = Polygon(
                    listOf(
                        Point(0.0, 0.0),
                        Point(width, 0.0),
                        Point(width, height),
                        Point(0.0, height)
                    )
                )
            )
        )
}
