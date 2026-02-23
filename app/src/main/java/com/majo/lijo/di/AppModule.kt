package com.majo.lijo.di

import android.content.Context
import androidx.room.Room
import com.majo.lijo.data.local.AppDatabase // <-- Твой пакет
import com.majo.lijo.data.local.ListItemDao
import com.majo.lijo.data.local.TaskListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "todo_db"
        ).addMigrations(AppDatabase.MIGRATION_1_2)   // добавляем миграцию
            .build()
    }

    @Provides
    fun provideTaskListDao(db: AppDatabase): TaskListDao = db.taskListDao()

    @Provides
    fun provideListItemDao(db: AppDatabase): ListItemDao = db.listItemDao()
}