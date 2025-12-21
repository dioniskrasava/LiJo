package com.majo.lijo.data.local

import androidx.room.*
import com.majo.lijo.data.local.entities.ListItem
import com.majo.lijo.data.local.entities.TaskList
import kotlinx.coroutines.flow.Flow

@Dao
interface ListItemDao {

    // Сортировка:
    // 1. Сначала по статусу (0 - false, 1 - true). Невыполненные будут сверху.
    // 2. Для выполненных: сортируем по времени выполнения (completedAt).
    // 3. Для невыполненных: сортируем по времени создания (createdAt DESC - новые сверху).
    @Query("""
        SELECT * FROM items 
        WHERE listId = :listId 
        ORDER BY 
          isCompleted ASC, 
          CASE WHEN isCompleted = 1 THEN completedAt END ASC,
          CASE WHEN isCompleted = 0 THEN createdAt END DESC
    """)
    fun getItemsForList(listId: Long): Flow<List<ListItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ListItem): Long

    @Update
    suspend fun updateItem(item: ListItem)

    @Query("DELETE FROM items WHERE itemId = :itemId")
    suspend fun deleteItem(itemId: Long)
}

