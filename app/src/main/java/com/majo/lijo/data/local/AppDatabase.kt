package com.majo.lijo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.majo.lijo.data.local.entities.ListItem // <-- Твой пакет
import com.majo.lijo.data.local.entities.TaskList // <-- Твой пакет

@Database(entities = [TaskList::class, ListItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskListDao(): TaskListDao
    abstract fun listItemDao(): ListItemDao
}