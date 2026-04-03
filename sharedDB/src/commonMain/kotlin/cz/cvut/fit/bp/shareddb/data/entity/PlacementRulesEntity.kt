package cz.cvut.fit.bp.shareddb.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "placement_rules")
data class PlacementRulesEntity(
    @PrimaryKey
    val projectId: String,
    val allowMirror: Boolean,
    val allowedAnglesJson: String
)
