package com.majo.lijo.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.Edit
import com.majo.lijo.data.local.entities.TaskList
import com.majo.lijo.ui.components.AddItemDialog
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

/**
 * Главный экран приложения, отображающий список всех категорий задач.
 *
 * @param viewModel Вью-модель для управления данными списков.
 * @param onListClick Лямбда-выражение, вызываемое при нажатии на карточку списка (для навигации).
 * @param onSettingsClick Лямбда-выражение для перехода на экран настроек.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onListClick: (Long) -> Unit,
    onSettingsClick: () -> Unit
) {
    // Подписка на поток данных из ViewModel
    val lists by viewModel.lists.collectAsState()

    // Состояние управления диалогом создания нового списка
    var showCreateDialog by remember { mutableStateOf(false) }

    // Переменная, которая хранит список, выбранный для удаления (null, если ничего не удаляем)
    var listPendingDeletion by remember { mutableStateOf<TaskList?>(null) }

    // Состояние для управления выпадающим меню в TopAppBar
    var showMenu by remember { mutableStateOf(false) }

    // хранит значение того списка, что мы собрались редактировать
    var listToEdit by remember { mutableStateOf<TaskList?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои списки") },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Меню")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Настройки") },
                            onClick = {
                                showMenu = false
                                onSettingsClick()
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Новый список")
            }
        }
    ) { padding ->
        // Проверка на наличие данных: если пусто — показываем заглушку
        if (lists.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Создайте первый список!", style = MaterialTheme.typography.headlineSmall)
            }
        } else {
            // Список карточек с оптимизацией через ключ (key) для стабильной анимации
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(lists, key = { it.taskList.listId }) { itemWithCount ->
                    TaskListCard(
                        taskList = itemWithCount.taskList,
                        taskCount = itemWithCount.taskCount, // Передаем реальное число из БД
                        onClick = { onListClick(itemWithCount.taskList.listId) },
                        onDelete = { listPendingDeletion = itemWithCount.taskList },
                        onEdit = { listToEdit = itemWithCount.taskList }
                    )
                }
            }
        }

        // Диалог создания нового списка
        if (showCreateDialog) {
            AddItemDialog(
                title = "Новый список",
                onDismiss = { showCreateDialog = false },
                onConfirm = { name ->
                    viewModel.createList(name)
                    showCreateDialog = false
                }
            )
        }

        // Алерт для подтверждения удаления категории
        if (listPendingDeletion != null) {
            AlertDialog(
                onDismissRequest = { listPendingDeletion = null },
                title = { Text("Удалить список?") },
                text = {
                    Text("Вы уверены, что хотите удалить «${listPendingDeletion?.name}»? " +
                            "Все задачи внутри него также будут удалены.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            listPendingDeletion?.let { viewModel.deleteList(it) }
                            listPendingDeletion = null // Сброс состояния после удаления
                        }
                    ) {
                        Text("Удалить", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { listPendingDeletion = null }) {
                        Text("Отмена")
                    }
                }
            )
        }


        // Диалог редактирования названия списка
        if (listToEdit != null) {
            AddItemDialog(
                title = "Редактировать список",
                initialText = listToEdit!!.name, // Нужно будет добавить такой параметр в AddItemDialog
                onDismiss = { listToEdit = null },
                onConfirm = { newName ->
                    viewModel.updateList(listToEdit!!, newName)
                    listToEdit = null
                }
            )
        }
    }
}

/**
 * Компонент карточки отдельного списка задач.
 *
 * @param taskList Объект данных списка.
 * @param onClick Обработка клика по карточке.
 * @param onDelete Обработка клика по иконке удаления.
 */
@Composable
fun TaskListCard(
    taskList: TaskList,
    taskCount: Int, // Добавляем новый параметр для количества задач
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = taskList.name, style = MaterialTheme.typography.titleMedium)


                // Вместо ID теперь показываем количество заметок
                Text(
                    text = when {
                        taskCount == 0 -> "Нет задач"
                        taskCount % 10 == 1 && taskCount % 100 != 11 -> "$taskCount задача"
                        taskCount % 10 in 2..4 && taskCount % 100 !in 12..14 -> "$taskCount задачи"
                        else -> "$taskCount задач"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Кнопка редактирования
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Редактировать")
            }

            // Кнопка удаления
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}