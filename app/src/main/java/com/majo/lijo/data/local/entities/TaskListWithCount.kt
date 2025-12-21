package com.majo.lijo.data.local.entities

import androidx.room.Embedded

/**
 * Объект для передачи данных (DTO).
 * Аннотация @Embedded говорит Room, что поля TaskList
 * находятся в том же результате запроса, что и taskCount.
 */
data class TaskListWithCount(
    @Embedded val taskList: TaskList, // Важно добавить @Embedded
    val taskCount: Int
)