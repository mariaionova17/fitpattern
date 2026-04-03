package cz.cvut.fit.bp.shareddb.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cz.cvut.fit.bp.shareddb.data.entity.PatternEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternSizeEntity

@Dao
interface PatternDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPattern(pattern: PatternEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSizes(sizes: List<PatternSizeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPieces(pieces: List<PatternPieceEntity>)

    @Query("SELECT * FROM patterns WHERE id = :patternId")
    suspend fun getPatternById(patternId: String): PatternEntity?

    @Query("SELECT * FROM pattern_sizes WHERE patternId = :patternId")
    suspend fun getSizesByPatternId(patternId: String): List<PatternSizeEntity>

    @Query("SELECT * FROM pattern_pieces WHERE patternId = :patternId")
    suspend fun getPiecesByPatternId(patternId: String): List<PatternPieceEntity>

    @Query("SELECT * FROM patterns")
    suspend fun getAllPatterns(): List<PatternEntity>

    @Query("DELETE FROM pattern_sizes WHERE patternId = :patternId")
    suspend fun deleteSizesByPatternId(patternId: String)

    @Query("DELETE FROM pattern_pieces WHERE patternId = :patternId")
    suspend fun deletePiecesByPatternId(patternId: String)

    @Query("DELETE FROM patterns WHERE id = :patternId")
    suspend fun deletePatternById(patternId: String)
}