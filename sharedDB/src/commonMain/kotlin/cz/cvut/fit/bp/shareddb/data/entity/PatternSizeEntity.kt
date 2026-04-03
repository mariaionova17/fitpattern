package cz.cvut.fit.bp.shareddb.data.entity

import androidx.room.Entity

@Entity(
    tableName = "pattern_sizes",
    primaryKeys = ["patternId", "size"]
)
data class PatternSizeEntity(
    val patternId: String,
    val size: String
)