package cz.cvut.fit.bp.shareddb.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fabric_pieces")
data class FabricPieceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val projectId: String,
    val contourJson: String,
    val holesJson: String
)
