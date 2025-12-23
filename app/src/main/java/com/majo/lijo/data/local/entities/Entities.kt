package com.majo.lijo.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Embedded

/**
 * Сущность, описывающая таблицу "lists" (Списки дел).
 */
@Entity(tableName = "lists")
data class TaskList(
    @PrimaryKey(autoGenerate = true)
    val listId: Long = 0, // Уникальный идентификатор списка
    val name: String, // Название списка (например, "Покупки")
    val color: Long? = null, // Цвет списка в формате HEX (Long) для UI
    val createdAt: Long = System.currentTimeMillis() // Время создания для сортировки
)




/**
 * Сущность, описывающая таблицу "items" (Конкретные задачи).
 * * Включает Foreign Key (Внешний ключ):
 * Связывает задачу со списком через listId.
 * onDelete = ForeignKey.CASCADE означает, что при удалении списка
 * все его задачи удалятся автоматически.
 */
@Entity(
    tableName = "items",
    foreignKeys = [ForeignKey(
        entity = TaskList::class,
        parentColumns = ["listId"],
        childColumns = ["listId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["listId"])] // Индекс ускоряет поиск задач по конкретному списку
)
data class ListItem(
    @PrimaryKey(autoGenerate = true)
    val itemId: Long = 0, // Уникальный ID задачи
    val listId: Long, // ID родительского списка
    val title: String, // Текст задачи
    val isCompleted: Boolean = false, // Статус выполнения

    /**
     * Позиция в списке.
     * Задел на будущее для реализации Drag-and-Drop перетаскивания.
     */
    val position: Int = 0,
    val createdAt: Long = System.currentTimeMillis(), // Время создания
    val completedAt: Long? = null // Время выполнения (заполняется при isCompleted = true)
)




/**
 * Объект для передачи данных (POJO/DTO).
 * Не является таблицей, используется для получения списка вместе с агрегированными данными.
 */
data class TaskListWithCount(
    /**
     * @Embedded "разворачивает" поля TaskList прямо в текущий объект.
     * Это позволяет Room сопоставить результат SQL-запроса с вложенным объектом.
     */
    @Embedded
    val taskList: TaskList,

    /**
     * Количество задач в данном списке.
     * Рассчитывается в TaskListDao с помощью функции COUNT().
     */
    val taskCount: Int
)