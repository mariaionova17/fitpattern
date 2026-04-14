package cz.cvut.fit.bp.shareddb.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nesting_results")
data class NestingResultEntity(
    @PrimaryKey
    val id: String,
    val projectId: String,
    val patternVariantId: String,
    val layoutVersionIndex: Int,
)