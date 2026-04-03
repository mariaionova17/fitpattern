package cz.cvut.fit.bp.shareddb.data.db

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun createDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder<AppDatabase>(
        context = context,
        name = "app_database.db"
    )
        .setDriver(BundledSQLiteDriver())
        .build()
}