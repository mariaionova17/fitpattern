package cz.cvut.fit.bp.shareddb.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cz.cvut.fit.bp.shareddb.data.entity.FabricPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.PlacementRulesEntity
import cz.cvut.fit.bp.shareddb.data.entity.ProjectEntity

@Dao
interface ProjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: PlacementRulesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFabrics(fabrics: List<FabricPieceEntity>)

    @Query("SELECT * FROM projects WHERE id = :projectId")
    suspend fun getProjectById(projectId: String): ProjectEntity?

    @Query("SELECT * FROM placement_rules WHERE projectId = :projectId")
    suspend fun getRulesByProjectId(projectId: String): PlacementRulesEntity?

    @Query("SELECT * FROM fabric_pieces WHERE projectId = :projectId")
    suspend fun getFabricsByProjectId(projectId: String): List<FabricPieceEntity>

    @Query("SELECT * FROM projects")
    suspend fun getAllProjects(): List<ProjectEntity>

    @Query("DELETE FROM fabric_pieces WHERE projectId = :projectId")
    suspend fun deleteFabricsByProjectId(projectId: String)

    @Query("DELETE FROM placement_rules WHERE projectId = :projectId")
    suspend fun deleteRulesByProjectId(projectId: String)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: String)
}