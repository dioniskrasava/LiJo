package com.majo.lijo.data.repository

import com.majo.lijo.data.local.ListItemDao
import com.majo.lijo.data.local.TaskListDao
import com.majo.lijo.data.local.entities.ListItem
import com.majo.lijo.data.local.entities.TaskList
import com.majo.lijo.data.local.entities.TaskListWithCount
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val listDao: TaskListDao,
    private val itemDao: ListItemDao
) {
    // Lists
    val allLists: Flow<List<TaskListWithCount>> = listDao.getAllListsWithCount()
    suspend fun createList(name: String) = listDao.insertList(TaskList(name = name))
    suspend fun deleteList(taskList: TaskList) = listDao.deleteList(taskList)

    // Items
    fun getItems(listId: Long): Flow<List<ListItem>> = itemDao.getItemsForList(listId)

    suspend fun addItem(listId: Long, title: String) {
        // Position можно вычислять отдельно, если нужен ручной Drag&Drop
        itemDao.insertItem(ListItem(listId = listId, title = title))
    }

    suspend fun updateItem(item: ListItem) = itemDao.updateItem(item)

    // Логика переключения статуса
    suspend fun toggleItemCompletion(item: ListItem) {
        val newStatus = !item.isCompleted
        val completedTime = if (newStatus) System.currentTimeMillis() else null

        // Мы обновляем только статус и время.
        // Позиция в UI изменится автоматически благодаря SQL сортировке в DAO.
        val updatedItem = item.copy(
            isCompleted = newStatus,
            completedAt = completedTime
        )
        itemDao.updateItem(updatedItem)
    }

    suspend fun updateList(taskList: TaskList) = listDao.updateList(taskList)
}