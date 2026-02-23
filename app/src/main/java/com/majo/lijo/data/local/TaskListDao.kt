package com.majo.lijo.data.local

import androidx.room.*
import com.majo.lijo.data.local.entities.TaskList
import com.majo.lijo.data.local.entities.TaskListWithCount
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskListDao {

    /**
     * Получает все списки вместе с количеством задач в каждом из них.
     * Использует LEFT JOIN, чтобы вернуть список даже если в нем 0 задач.
     */
    @Query("""
        SELECT lists.*, COUNT(items.itemId) as taskCount 
        FROM lists 
        LEFT JOIN items ON lists.listId = items.listId 
        GROUP BY lists.listId 
        ORDER BY lists.position ASC, lists.createdAt DESC
    """)
    fun getAllListsWithCount(): Flow<List<TaskListWithCount>>

    /**
     * Обычное получение всех списков (оставим для совместимости, если нужно)
     */
    @Query("SELECT * FROM lists ORDER BY position ASC, createdAt DESC")
    fun getAllLists(): Flow<List<TaskList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(taskList: TaskList): Long

    @Delete
    suspend fun deleteList(taskList: TaskList)

    @Update
    suspend fun updateList(taskList: TaskList)

    // Массовое обновление для изменения порядка
    @Update
    suspend fun updateLists(vararg taskLists: TaskList)
}