package cz.cvut.fit.bp.shareddb.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import cz.cvut.fit.bp.shareddb.data.entity.LayoutPlacedPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.NestingResultEntity

data class NestingResultWithPieces(
    @Embedded
    val result: NestingResultEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "nestingResultId"
    )
    val placedPieces: List<LayoutPlacedPieceEntity>,
)