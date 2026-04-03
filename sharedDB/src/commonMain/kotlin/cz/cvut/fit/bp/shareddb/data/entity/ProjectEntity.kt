package cz.cvut.fit.bp.shareddb.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val seamAllowance: Double
)