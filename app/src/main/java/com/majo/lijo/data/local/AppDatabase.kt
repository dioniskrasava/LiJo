package com.majo.lijo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.majo.lijo.data.local.entities.ListItem // <-- Твой пакет
import com.majo.lijo.data.local.entities.TaskList // <-- Твой пакет


@Database(entities = [TaskList::class, ListItem::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskListDao(): TaskListDao
    abstract fun listItemDao(): ListItemDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE lists ADD COLUMN icon TEXT")
                database.execSQL("ALTER TABLE lists ADD COLUMN position INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}