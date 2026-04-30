package cz.cvut.fit.bp.shareddb.data.repository

import cz.cvut.fit.bp.shareddb.data.entity.LayoutPlacedPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.NestingResultEntity
import cz.cvut.fit.bp.shareddb.data.relation.NestingResultWithPieces
import domain.geometry.Point
import domain.geometry.Transformation
import domain.layout.LayoutScore
import domain.layout.LayoutState
import domain.layout.Placement
import domain.solver.NestingResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NestingResultRepositoryImplTest {

    @Test
    fun `saveNestingResult stores next layout version and placed pieces`() = runTest {
        val dao = FakeNestingResultDao()
        dao.maxIndexByProjectVariant["project-1" to "variant-1"] = 4

        val repository = NestingResultRepositoryImpl(dao)

        repository.saveNestingResult(
            nestingResult = sampleNestingResult(),
            projectId = "project-1",
            patternVariantId = "variant-1"
        )

        val stored = dao.results["result-1"]
        assertEquals("result-1", stored?.id)
        assertEquals("project-1", stored?.projectId)
        assertEquals("variant-1", stored?.patternVariantId)
        assertEquals(5, stored?.layoutVersionIndex)

        val placed = dao.placedPiecesByResultId["result-1"].orEmpty()
        assertEquals(1, placed.size)
        assertEquals("placement-1", placed.single().id)
        assertEquals("piece-1", placed.single().pieceId)

        assertEquals(
            listOf("insertNestingResult:result-1", "insertPlacedPieces:result-1"),
            dao.callLog
        )
    }

    @Test
    fun `getNestingResultsByProjectAndVariant maps results and skips missing relations`() = runTest {
        val dao = FakeNestingResultDao()

        dao.results["result-1"] = NestingResultEntity("result-1", "project-1", "variant-1", 1)
        dao.results["result-2"] = NestingResultEntity("result-2", "project-1", "variant-1", 2)

        dao.relationsById["result-1"] = relation(
            resultId = "result-1",
            projectId = "project-1",
            patternVariantId = "variant-1",
            layoutVersionIndex = 1,
            placementId = "placement-1",
            pieceId = "piece-1",
            fabricPieceIndex = 0
        )
        dao.relationsById["result-2"] = null

        val repository = NestingResultRepositoryImpl(dao)

        val results = repository.getNestingResultsByProjectAndVariant("project-1", "variant-1")

        assertEquals(1, results.size)
        assertEquals("result-1", results.single().id)
        assertEquals("piece-1", results.single().bestLayout.placed.single().pieceId)
    }

    @Test
    fun `getNestingResultsByProject maps all available relations`() = runTest {
        val dao = FakeNestingResultDao()

        dao.results["result-1"] = NestingResultEntity("result-1", "project-1", "variant-1", 2)
        dao.results["result-2"] = NestingResultEntity("result-2", "project-1", "variant-2", 1)

        dao.relationsById["result-1"] = relation(
            resultId = "result-1",
            projectId = "project-1",
            patternVariantId = "variant-1",
            layoutVersionIndex = 2,
            placementId = "placement-1",
            pieceId = "piece-1",
            fabricPieceIndex = 0
        )
        dao.relationsById["result-2"] = relation(
            resultId = "result-2",
            projectId = "project-1",
            patternVariantId = "variant-2",
            layoutVersionIndex = 1,
            placementId = "placement-2",
            pieceId = "piece-2",
            fabricPieceIndex = 1
        )

        val repository = NestingResultRepositoryImpl(dao)

        val results = repository.getNestingResultsByProject("project-1")

        assertEquals(2, results.size)
        assertEquals(listOf("result-2", "result-1"), results.map { it.id })
    }

    @Test
    fun `getPatternNameAndVariantIdFromNestingResult returns values when both exist`() = runTest {
        val dao = FakeNestingResultDao()
        dao.patternNameByResultId["result-1"] = "Basic T-Shirt"
        dao.patternVariantIdByResultId["result-1"] = "variant-m"

        val repository = NestingResultRepositoryImpl(dao)

        val result = repository.getPatternNameAndVariantIdFromNestingResult("result-1")

        assertEquals("Basic T-Shirt" to "variant-m", result)
    }

    @Test
    fun `getPatternNameAndVariantIdFromNestingResult returns empty pair when some value is missing`() = runTest {
        val dao = FakeNestingResultDao()
        dao.patternNameByResultId["result-1"] = "Basic T-Shirt"
        dao.patternVariantIdByResultId["result-1"] = null

        val repository = NestingResultRepositoryImpl(dao)

        val result = repository.getPatternNameAndVariantIdFromNestingResult("result-1")

        assertEquals("" to "", result)
    }

    @Test
    fun `deleteNestingResultsByProject deletes placed pieces before results`() = runTest {
        val dao = FakeNestingResultDao()
        val repository = NestingResultRepositoryImpl(dao)

        repository.deleteNestingResultsByProject("project-1")

        assertEquals(
            listOf(
                "deletePlacedPiecesByProject:project-1",
                "deleteNestingResultsByProject:project-1"
            ),
            dao.callLog
        )
    }

    @Test
    fun `getNestingResultsByProjectAndVariant returns empty list when no results exist`() = runTest {
        val repository = NestingResultRepositoryImpl(FakeNestingResultDao())

        val results = repository.getNestingResultsByProjectAndVariant("missing", "variant")

        assertTrue(results.isEmpty())
    }

    private fun sampleNestingResult(): NestingResult =
        NestingResult(
            id = "result-1",
            bestLayout = LayoutState(
                placed = listOf(
                    Placement(
                        id = "placement-1",
                        pieceId = "piece-1",
                        fabricPieceIndex = 1,
                        transformation = Transformation(
                            move = Point(10.0, 15.0),
                            rotationDegrees = 90.0,
                            mirrorY = true
                        )
                    )
                ),
                unplaced = listOf("piece-x"),
                score = LayoutScore(10.0, 20.0)
            ),
            triedLayouts = 99,
            timeMs = 1234
        )

    private fun relation(
        resultId: String,
        projectId: String,
        patternVariantId: String,
        layoutVersionIndex: Int,
        placementId: String,
        pieceId: String,
        fabricPieceIndex: Int
    ): NestingResultWithPieces =
        NestingResultWithPieces(
            result = NestingResultEntity(
                id = resultId,
                projectId = projectId,
                patternVariantId = patternVariantId,
                layoutVersionIndex = layoutVersionIndex
            ),
            placedPieces = listOf(
                LayoutPlacedPieceEntity(
                    id = placementId,
                    nestingResultId = resultId,
                    pieceId = pieceId,
                    fabricPieceIndex = fabricPieceIndex,
                    transformationJson = """{"move":{"x":1.0,"y":2.0},"rotationDegrees":0.0,"mirrorY":false}"""
                )
            )
        )
}
