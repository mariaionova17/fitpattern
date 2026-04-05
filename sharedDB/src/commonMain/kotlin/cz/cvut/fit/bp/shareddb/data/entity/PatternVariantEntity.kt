package cz.cvut.fit.bp.shareddb.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pattern_variants")
data class PatternVariantEntity(
    @PrimaryKey
    val variantId: String,
    val patternId: String,
    val size: String
)