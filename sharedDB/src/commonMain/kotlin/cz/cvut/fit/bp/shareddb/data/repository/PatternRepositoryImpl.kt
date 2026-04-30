package cz.cvut.fit.bp.shareddb.data.repository

import cz.cvut.fit.bp.shareddb.data.dao.PatternDao
import cz.cvut.fit.bp.shareddb.data.db.AppDatabase
import cz.cvut.fit.bp.shareddb.data.entity.PatternEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternVariantEntity
import cz.cvut.fit.bp.shareddb.data.mapper.toPattern
import cz.cvut.fit.bp.shareddb.data.mapper.toPatternPiece
import cz.cvut.fit.bp.shareddb.data.mapper.toPatternPieceEntity
import cz.cvut.fit.bp.shareddb.data.mapper.toPatternVariant
import domain.pattern.Pattern
import domain.pattern.PatternPiece
import domain.repository.PatternRepository

class PatternRepositoryImpl(
    private val dao: PatternDao
) : PatternRepository {

    constructor(db: AppDatabase) : this(db.patternDao())

    override suspend fun savePattern(pattern: Pattern) {
        dao.insertPattern(
            PatternEntity(
                id = pattern.id,
                name = pattern.name,
                pictureUri = pattern.pictureUri,
                downloadLink = pattern.downloadLink
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
        val patternEntity = dao.getPatternById(id) ?: return null

        val variants = dao.getVariantsByPatternId(id).map { variantEntity ->
            val pieces = dao.getPiecesByVariantId(variantEntity.variantId)
            variantEntity.toPatternVariant(pieces)
        }

        return patternEntity.toPattern(variants)
    }

    override suspend fun getPatternByVariantId(variantId: String): List<PatternPiece> {
        return dao.getPiecesByVariantId(variantId).map { it.toPatternPiece() }
    }

    override suspend fun getSizeByVariantId(variantId: String): String? {
        return dao.getSizeByVariantId(variantId)
    }

    override suspend fun getAllPatterns(): List<Pattern> {
        return dao.getAllPatterns().map { patternEntity ->
            val variants = dao.getVariantsByPatternId(patternEntity.id).map { variantEntity ->
                val pieces = dao.getPiecesByVariantId(variantEntity.variantId)
                variantEntity.toPatternVariant(pieces)
            }

            patternEntity.toPattern(variants)
        }
    }

    override suspend fun deletePattern(id: String) {
        dao.deletePatternById(id)
    }
}