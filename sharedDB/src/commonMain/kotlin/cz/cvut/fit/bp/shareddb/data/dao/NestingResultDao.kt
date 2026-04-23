package cz.cvut.fit.bp.shareddb.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import cz.cvut.fit.bp.shareddb.data.entity.LayoutPlacedPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.NestingResultEntity
import cz.cvut.fit.bp.shareddb.data.relation.NestingResultWithPieces

@Dao
interface NestingResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNestingResult(result: NestingResultEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlacedPieces(pieces: List<LayoutPlacedPieceEntity>)

    @Transaction
    @Query("SELECT * FROM nesting_results WHERE id = :nestingResultId")
    suspend fun getNestingResultWithPieces(nestingResultId: String): NestingResultWithPieces?

    @Query(
        """
        SELECT * FROM nesting_results
        WHERE projectId = :projectId AND patternVariantId = :patternVariantId
        ORDER BY layoutVersionIndex ASC
        """
    )
    suspend fun getResultsForProjectAndVariant(
        projectId: String,
        patternVariantId: String
    ): List<NestingResultEntity>

    @Query(
        """
        SELECT * FROM nesting_results
        WHERE projectId = :projectId
        ORDER BY layoutVersionIndex ASC
        """
    )
    suspend fun getResultsForProject(
        projectId: String
    ): List<NestingResultEntity>

    @Query("DELETE FROM nesting_results WHERE id = :nestingResultId")
    suspend fun deleteNestingResult(nestingResultId: String)

    @Query(
        """
    SELECT COALESCE(MAX(layoutVersionIndex), 0)
    FROM nesting_results
    WHERE projectId = :projectId AND patternVariantId = :patternVariantId
    """
    )
    suspend fun getMaxLayoutVersionIndex(
        projectId: String,
        patternVariantId: String
    ): Int

    @Query(
        """
        SELECT a.variantId FROM pattern_variants a JOIN nesting_results b ON a.variantId = b.patternVariantId 
        WHERE id = :nestingResultId
        """
    )
    suspend fun getPatternVariantIdFromNestingResult(
        nestingResultId: String
    ): String?

    @Query(
        """
        SELECT name FROM patterns a JOIN pattern_variants b ON a.id = b.patternId 
        JOIN  nesting_results c ON b.variantId = c.patternVariantId 
        WHERE c.id = :nestingResultId
        """
    )
    suspend fun getPatternNameFromNestingResult(
        nestingResultId: String
    ): String?

    // nestingResultId -> nesting_res -> patternVariantId -> pattern_variants -> size
    // nestingResultId -> nesting_res -> patternVariantId -> pattern_variants -> pattern_id -> patterns -> name

    @Query(
        """
    DELETE FROM layout_placements
    WHERE nestingResultId IN (
        SELECT id FROM nesting_results WHERE projectId = :projectId
    )
    """
    )
    suspend fun deletePlacedPiecesByProjectId(projectId: String)

    @Query("DELETE FROM nesting_results WHERE projectId = :projectId")
    suspend fun deleteNestingResultsByProjectId(projectId: String)
}