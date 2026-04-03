package cz.cvut.fit.bp.shareddb.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import cz.cvut.fit.bp.shareddb.data.dao.PatternDao
import cz.cvut.fit.bp.shareddb.data.dao.ProjectDao
import cz.cvut.fit.bp.shareddb.data.entity.FabricPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternPieceEntity
import cz.cvut.fit.bp.shareddb.data.entity.PatternSizeEntity
import cz.cvut.fit.bp.shareddb.data.entity.PlacementRulesEntity
import cz.cvut.fit.bp.shareddb.data.entity.ProjectEntity


@Database(
    entities = [
        ProjectEntity::class,
        PlacementRulesEntity::class,
        FabricPieceEntity::class,
        PatternEntity::class,
        PatternSizeEntity::class,
        PatternPieceEntity::class
    ],
    version = 1,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun patternDao(): PatternDao
}