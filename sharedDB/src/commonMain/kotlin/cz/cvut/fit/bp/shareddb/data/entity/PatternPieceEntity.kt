package cz.cvut.fit.bp.shareddb.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pattern_pieces")
data class PatternPieceEntity(
    @PrimaryKey
    val id: String,
    val patternId: String,
    val contourJson: String,
    val holesJson: String
)