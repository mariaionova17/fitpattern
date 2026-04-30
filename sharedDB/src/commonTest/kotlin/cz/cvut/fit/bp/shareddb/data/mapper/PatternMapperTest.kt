package cz.cvut.fit.bp.shareddb.data.mapper

import cz.cvut.fit.bp.shareddb.data.entity.PatternEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternVariantEntity
import domain.geometry.Point
import domain.geometry.Polygon
import domain.geometry.Shape
import domain.pattern.PatternPiece
import domain.pattern.PatternVariant
import kotlin.test.Test
import kotlin.test.assertEquals

class PatternMapperTest {

    @Test
    fun `PatternPieceEntity converts to domain PatternPiece`() {
        val contour = rectangle(0.0, 0.0, 4.0, 3.0)
        val holes = listOf(rectangle(1.0, 1.0, 2.0, 2.0))

        val entity = PatternPieceEntity(
            id = "piece-1",
            name = "Front",
            patternVariantId = "variant-1",
            contourJson = PolygonMapper.polygonToJson(contour),
            holesJson = PolygonMapper.polygonsToJson(holes)
        )

        val piece = entity.toPatternPiece()

        assertEquals("piece-1", piece.id)
        assertEquals("Front", piece.name)
        assertEquals(Shape(contour = contour, holes = holes), piece.shape)
    }

    @Test
    fun `PatternPiece converts to entity with serialized geometry`() {
        val piece = PatternPiece(
            id = "piece-1",
            name = "Front",
            shape = Shape(
                contour = rectangle(0.0, 0.0, 4.0, 3.0),
                holes = listOf(rectangle(1.0, 1.0, 2.0, 2.0))
            )
        )

        val entity = piece.toPatternPieceEntity("variant-1")

        assertEquals("piece-1", entity.id)
        assertEquals("Front", entity.name)
        assertEquals("variant-1", entity.patternVariantId)
        assertEquals(piece.shape.contour, PolygonMapper.polygonFromJson(entity.contourJson))
        assertEquals(piece.shape.holes, PolygonMapper.polygonsFromJson(entity.holesJson))
    }

    @Test
    fun `PatternVariantEntity converts to domain PatternVariant with mapped pieces`() {
        val pieceEntity = PatternPieceEntity(
            id = "piece-1",
            name = "Front",
            patternVariantId = "variant-1",
            contourJson = PolygonMapper.polygonToJson(rectangle(0.0, 0.0, 4.0, 3.0)),
            holesJson = PolygonMapper.polygonsToJson(emptyList())
        )

        val variantEntity = PatternVariantEntity(
            variantId = "variant-1",
            patternId = "pattern-1",
            size = "M"
        )

        val variant = variantEntity.toPatternVariant(listOf(pieceEntity))

        assertEquals("variant-1", variant.id)
        assertEquals("M", variant.size)
        assertEquals(1, variant.pieces.size)
        assertEquals("piece-1", variant.pieces.single().id)
    }

    @Test
    fun `PatternEntity converts to domain Pattern with provided variants`() {
        val variants = listOf(
            PatternVariant(
                id = "variant-s",
                size = "S",
                pieces = emptyList()
            ),
            PatternVariant(
                id = "variant-m",
                size = "M",
                pieces = emptyList()
            )
        )

        val entity = PatternEntity(
            id = "pattern-1",
            name = "Basic T-Shirt",
            pictureUri = "content://image",
            downloadLink = "https://example.com/pattern"
        )

        val pattern = entity.toPattern(variants)

        assertEquals("pattern-1", pattern.id)
        assertEquals("Basic T-Shirt", pattern.name)
        assertEquals("content://image", pattern.pictureUri)
        assertEquals("https://example.com/pattern", pattern.downloadLink)
        assertEquals(variants, pattern.variants)
    }

    private fun rectangle(minX: Double, minY: Double, maxX: Double, maxY: Double): Polygon =
        Polygon(
            listOf(
                Point(minX, minY),
                Point(maxX, minY),
                Point(maxX, maxY),
                Point(minX, maxY)
            )
        )
}
