package cz.cvut.fit.bp.shareddb.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pattern_pieces")
data class PatternPieceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val patternVariantId: String,
    val contourJson: String,
    val holesJson: String
)