package cz.cvut.fit.bp.shareddb.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cz.cvut.fit.bp.shareddb.data.entity.PatternEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternVariantEntity

@Dao
interface PatternDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPattern(pattern: PatternEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariants(variants: List<PatternVariantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPieces(pieces: List<PatternPieceEntity>)

    @Query("SELECT * FROM patterns WHERE id = :patternId")
    suspend fun getPatternById(patternId: String): PatternEntity?

    @Query("SELECT size FROM pattern_variants WHERE variantId = :variantId")
    suspend fun getSizeByVariantId(variantId: String): String?

    @Query("SELECT * FROM pattern_variants WHERE patternId = :patternId")
    suspend fun getVariantsByPatternId(patternId: String): List<PatternVariantEntity>

    @Query("SELECT * FROM pattern_pieces WHERE patternVariantId = :variantId")
    suspend fun getPiecesByVariantId(variantId: String): List<PatternPieceEntity>

    @Query("SELECT * FROM patterns")
    suspend fun getAllPatterns(): List<PatternEntity>

    @Query("DELETE FROM patterns WHERE id = :patternId")
    suspend fun deletePatternById(patternId: String)
}