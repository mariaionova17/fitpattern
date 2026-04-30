package cz.cvut.fit.bp.shareddb.data.mapper

import cz.cvut.fit.bp.shareddb.data.entity.LayoutPlacedPieceEntity
import cz.cvut.fit.bp.shareddb.data.relation.NestingResultWithPieces
import domain.geometry.Point
import domain.geometry.Transformation
import domain.layout.LayoutScore
import domain.layout.LayoutState
import domain.layout.Placement
import domain.solver.NestingResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NestingResultMapperTest {

    @Test
    fun `toEntities creates result entity and placed piece entities`() {
        val nestingResult = NestingResult(
            id = "result-1",
            bestLayout = LayoutState(
                placed = listOf(
                    Placement(
                        id = "placement-1",
                        pieceId = "piece-1",
                        fabricPieceIndex = 2,
                        transformation = Transformation(
                            move = Point(10.0, 20.0),
                            rotationDegrees = 90.0,
                            mirrorY = true
                        )
                    )
                ),
                unplaced = listOf("piece-2"),
                score = LayoutScore(123.0, 456.0)
            ),
            triedLayouts = 50,
            timeMs = 999
        )

        val (resultEntity, placedEntities) = nestingResult.toEntities(
            projectId = "project-1",
            patternVariantId = "variant-1",
            layoutVersionIndex = 3
        )

        assertEquals("result-1", resultEntity.id)
        assertEquals("project-1", resultEntity.projectId)
        assertEquals("variant-1", resultEntity.patternVariantId)
        assertEquals(3, resultEntity.layoutVersionIndex)

        assertEquals(1, placedEntities.size)
        val placed = placedEntities.single()
        assertEquals("placement-1", placed.id)
        assertEquals("result-1", placed.nestingResultId)
        assertEquals("piece-1", placed.pieceId)
        assertEquals(2, placed.fabricPieceIndex)
        assertTrue(placed.transformationJson.isNotBlank())
    }

    @Test
    fun `toDomainPlacements restores transformations correctly`() {
        val entities = listOf(
            LayoutPlacedPieceEntity(
                id = "placement-1",
                nestingResultId = "result-1",
                pieceId = "piece-1",
                fabricPieceIndex = 2,
                transformationJson = """{"move":{"x":10.0,"y":20.0},"rotationDegrees":90.0,"mirrorY":true}"""
            )
        )

        val placements = toDomainPlacements(entities)

        assertEquals(1, placements.size)
        val placement = placements.single()
        assertEquals("placement-1", placement.id)
        assertEquals("piece-1", placement.pieceId)
        assertEquals(2, placement.fabricPieceIndex)
        assertEquals(Point(10.0, 20.0), placement.transformation.move)
        assertEquals(90.0, placement.transformation.rotationDegrees)
        assertEquals(true, placement.transformation.mirrorY)
    }

    @Test
    fun `NestingResultWithPieces converts to domain nesting result`() {
        val relation = NestingResultWithPieces(
            result = cz.cvut.fit.bp.shareddb.data.entity.NestingResultEntity(
                id = "result-1",
                projectId = "project-1",
                patternVariantId = "variant-1",
                layoutVersionIndex = 3
            ),
            placedPieces = listOf(
                LayoutPlacedPieceEntity(
                    id = "placement-1",
                    nestingResultId = "result-1",
                    pieceId = "piece-1",
                    fabricPieceIndex = 0,
                    transformationJson = """{"move":{"x":5.0,"y":6.0},"rotationDegrees":180.0,"mirrorY":false}"""
                )
            )
        )

        val domain = relation.toDomain()

        assertEquals("result-1", domain.id)
        assertEquals(1, domain.bestLayout.placed.size)

        val placement = domain.bestLayout.placed.single()
        assertEquals("placement-1", placement.id)
        assertEquals("piece-1", placement.pieceId)
        assertEquals(0, placement.fabricPieceIndex)
        assertEquals(Point(5.0, 6.0), placement.transformation.move)
        assertEquals(180.0, placement.transformation.rotationDegrees)
        assertEquals(false, placement.transformation.mirrorY)

        //TODO change behaviour
        assertTrue(domain.bestLayout.unplaced.isEmpty())
        assertEquals(LayoutScore(0.0, 0.0), domain.bestLayout.score)
        assertEquals(0, domain.triedLayouts)
        assertEquals(0, domain.timeMs)
    }
}
