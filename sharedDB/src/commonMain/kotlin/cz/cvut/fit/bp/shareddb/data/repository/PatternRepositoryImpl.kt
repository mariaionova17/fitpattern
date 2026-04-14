package cz.cvut.fit.bp.shareddb.data.repository

import cz.cvut.fit.bp.shareddb.data.db.AppDatabase
import cz.cvut.fit.bp.shareddb.data.entity.PatternEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternVariantEntity
import cz.cvut.fit.bp.shareddb.data.mapper.toPattern
import cz.cvut.fit.bp.shareddb.data.mapper.toPatternPieceEntity
import cz.cvut.fit.bp.shareddb.data.mapper.toPatternPiece
import cz.cvut.fit.bp.shareddb.data.mapper.toPatternVariant
import domain.pattern.Pattern
import domain.pattern.PatternPiece
import domain.repository.PatternRepository

class PatternRepositoryImpl(
    private val db: AppDatabase
) : PatternRepository {

    override suspend fun savePattern(pattern: Pattern) {
        val dao = db.patternDao()

        dao.insertPattern(
            PatternEntity(
                id = pattern.id,
                name = pattern.name,
                pictureUri = pattern.pictureUri
            )
        )

        val variantEntities = pattern.variants.map { variant ->
            PatternVariantEntity(
                variantId = variant.id,
                patternId = pattern.id,
                size = variant.size
            )
        }

        dao.insertVariants(variantEntities)

        val pieceEntities = pattern.variants.flatMap { variant ->
            variant.pieces.map { piece ->
                piece.toPatternPieceEntity(variant.id)
            }
        }

        dao.insertPieces(pieceEntities)
    }

    override suspend fun getPatternById(id: String): Pattern? {
        val dao = db.patternDao()
        val patternEntity = dao.getPatternById(id) ?: return null

        val variants = dao.getVariantsByPatternId(id).map { variantEntity ->
            val pieces = dao.getPiecesByVariantId(variantEntity.variantId)
            variantEntity.toPatternVariant(pieces)
        }

        return patternEntity.toPattern(variants)
    }

    override suspend fun getPatternByVariantId(variantId: String): List<PatternPiece>{
        val dao = db.patternDao()

        return dao.getPiecesByVariantId(variantId).map { patternEntity ->
            patternEntity.toPatternPiece()
        }
    }
    override suspend fun getSizeByVariantId(variantId: String): String?{
        val dao = db.patternDao()
        return dao.getSizeByVariantId(variantId)
    }


    override suspend fun getAllPatterns(): List<Pattern> {
        val dao = db.patternDao()

        return dao.getAllPatterns().map { patternEntity ->
            val variants = dao.getVariantsByPatternId(patternEntity.id).map { variantEntity ->
                val pieces = dao.getPiecesByVariantId(variantEntity.variantId)
                variantEntity.toPatternVariant(pieces)
            }

            patternEntity.toPattern(variants)
        }
    }

    override suspend fun deletePattern(id: String) {
        val dao = db.patternDao()
        dao.deletePatternById(id)
    }
}