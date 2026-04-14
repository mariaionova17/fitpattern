package cz.cvut.fit.bp.shareddb.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import cz.cvut.fit.bp.shareddb.data.dao.NestingResultDao
import cz.cvut.fit.bp.shareddb.data.dao.PatternDao
import cz.cvut.fit.bp.shareddb.data.dao.ProjectDao
import cz.cvut.fit.bp.shareddb.data.entity.FabricPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.LayoutPlacedPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.NestingResultEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternVariantEntity
import cz.cvut.fit.bp.shareddb.data.entity.PlacementRulesEntity
import cz.cvut.fit.bp.shareddb.data.entity.ProjectEntity


@Database(
    entities = [
        ProjectEntity::class,
        PlacementRulesEntity::class,
        FabricPieceEntity::class,
        PatternEntity::class,
        PatternVariantEntity::class,
        PatternPieceEntity::class,
        LayoutPlacedPieceEntity::class,
        NestingResultEntity::class
    ],
    version = 1,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun patternDao(): PatternDao
    abstract fun nestingResultDao(): NestingResultDao
}