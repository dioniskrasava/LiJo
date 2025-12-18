package com.majo.lijo.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "lists")
data class TaskList(
    @PrimaryKey(autoGenerate = true) val listId: Long = 0,
    val name: String,
    val color: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "items",
    foreignKeys = [ForeignKey(
        entity = TaskList::class,
        parentColumns = ["listId"],
        childColumns = ["listId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["listId"])]
)
data class ListItem(
    @PrimaryKey(autoGenerate = true) val itemId: Long = 0,
    val listId: Long,
    val title: String,
    val isCompleted: Boolean = false,
    // position пригодится для drag-n-drop, но для базовой логики "в конец"
    // нам хватит timestamps
    val position: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)