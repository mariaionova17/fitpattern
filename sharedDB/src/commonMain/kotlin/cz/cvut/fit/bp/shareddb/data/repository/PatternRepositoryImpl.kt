package cz.cvut.fit.bp.shareddb.data.repository

import cz.cvut.fit.bp.shareddb.data.db.AppDatabase
import cz.cvut.fit.bp.shareddb.data.entity.PatternEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternSizeEntity
import cz.cvut.fit.bp.shareddb.data.mapper.toPatternPiece
import cz.cvut.fit.bp.shareddb.data.mapper.toPatternPieceEntity
import domain.pattern.Pattern
import domain.repository.PatternRepository

class PatternRepositoryImpl(
    private val db: AppDatabase
) : PatternRepository {

    override suspend fun savePattern(pattern: Pattern) {
        val dao = db.patternDao()

        dao.insertPattern(
            PatternEntity(
                id = pattern.id,
                name = pattern.name
            )
        )

        dao.deleteSizesByPatternId(pattern.id)
        dao.deletePiecesByPatternId(pattern.id)

        dao.insertSizes(
            pattern.sizes.map { size ->
                PatternSizeEntity(
                    patternId = pattern.id,
                    size = size
                )
            }
        )

        dao.insertPieces(pattern.pieces.map { it.toPatternPieceEntity(pattern.id) })
    }

    override suspend fun getPatternById(id: String): Pattern? {
        val dao = db.patternDao()

        val patternEntity = dao.getPatternById(id) ?: return null
        val sizes = dao.getSizesByPatternId(id).map { it.size }
        val pieces = dao.getPiecesByPatternId(id).map { it.toPatternPiece() }

        return Pattern(
            id = patternEntity.id,
            name = patternEntity.name,
            sizes = sizes,
            pieces = pieces
        )
    }

    override suspend fun getAllPatterns(): List<Pattern> {
        val dao = db.patternDao()

        return dao.getAllPatterns().map { patternEntity ->
            Pattern(
                id = patternEntity.id,
                name = patternEntity.name,
                sizes = dao.getSizesByPatternId(patternEntity.id).map { it.size },
                pieces = dao.getPiecesByPatternId(patternEntity.id).map { it.toPatternPiece() }
            )
        }
    }

    override suspend fun deletePattern(id: String) {
        val dao = db.patternDao()
        dao.deleteSizesByPatternId(id)
        dao.deletePiecesByPatternId(id)
        dao.deletePatternById(id)
    }
}