package cz.cvut.fit.bp.shareddb.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "layout_placements")
data class LayoutPlacedPieceEntity(
    @PrimaryKey
    val id: String,
    val nestingResultId: String,
    val pieceId: String,
    val fabricPieceIndex: Int,
    val transformationJson: String
)