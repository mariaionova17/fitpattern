package cz.cvut.fit.bp.shareddb.data.mapper

import cz.cvut.fit.bp.shareddb.data.entity.LayoutPlacedPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.NestingResultEntity
import cz.cvut.fit.bp.shareddb.data.relation.NestingResultWithPieces
import domain.geometry.Transformation
import domain.layout.LayoutScore
import domain.layout.LayoutState
import domain.layout.Placement
import domain.solver.NestingResult
import kotlinx.serialization.json.Json


private val json = Json {
    ignoreUnknownKeys = true
}

private fun transformationToJson(transformation: Transformation): String {
    return json.encodeToString(transformation)
}

private fun transformationFromJson(raw: String): Transformation {
    return json.decodeFromString(raw)
}
fun NestingResult.toEntities(
    projectId: String,
    patternVariantId: String,
    layoutVersionIndex: Int,
): Pair<NestingResultEntity, List<LayoutPlacedPieceEntity>> {
    val nestingResultId = id

    val resultEntity = NestingResultEntity(
        id = nestingResultId,
        projectId = projectId,
        patternVariantId = patternVariantId,
        layoutVersionIndex = layoutVersionIndex
    )

    val placedEntities = bestLayout.placed.map { placement ->
        LayoutPlacedPieceEntity(
            id = placement.id,
            nestingResultId = nestingResultId,
            pieceId = placement.pieceId,
            fabricPieceIndex = placement.fabricPieceIndex,
            transformationJson = transformationToJson(placement.transformation)
        )
    }


    return Pair(resultEntity, placedEntities)
}

fun toDomainPlacements(
    placedEntities: List<LayoutPlacedPieceEntity>,
): List<Placement> {
    return placedEntities.map { entity ->
        Placement(
            id = entity.id,
            pieceId = entity.pieceId,
            fabricPieceIndex = entity.fabricPieceIndex,
            transformation = transformationFromJson(entity.transformationJson)
        )
    }
}

fun NestingResultWithPieces.toDomain(): NestingResult {
    val placed = placedPieces.map { entity ->
        Placement(
            id = entity.id,
            pieceId = entity.pieceId,
            fabricPieceIndex = entity.fabricPieceIndex,
            transformation = transformationFromJson(entity.transformationJson)
        )
    }

    return NestingResult(
        id = result.id,
        bestLayout = LayoutState(
            placed = placed,
            unplaced = emptyList(),
            score = LayoutScore(0.0, 0.0)
        ),
        triedLayouts = 0,
        timeMs = 0
    )
} //TODO score is disappearing (?)