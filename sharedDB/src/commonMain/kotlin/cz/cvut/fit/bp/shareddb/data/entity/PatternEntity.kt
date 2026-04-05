package cz.cvut.fit.bp.shareddb.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patterns")
data class PatternEntity (
    @PrimaryKey
    val id: String,
    val name: String,
    val pictureUri: String
)