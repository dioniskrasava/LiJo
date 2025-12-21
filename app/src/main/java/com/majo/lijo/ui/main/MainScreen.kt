package com.majo.lijo.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.majo.lijo.data.local.entities.TaskList
import com.majo.lijo.ui.components.AddItemDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onListClick: (Long) -> Unit,
    onSettingsClick: () -> Unit
) {
    val lists by viewModel.lists.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    // Состояния для управления операциями
    var showActionMenuFor by remember { mutableStateOf<TaskList?>(null) }
    var listToEdit by remember { mutableStateOf<TaskList?>(null) }
    var listPendingDeletion by remember { mutableStateOf<TaskList?>(null) }

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
        if (lists.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Создайте первый список!", style = MaterialTheme.typography.headlineSmall)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(lists, key = { it.taskList.listId }) { itemWithCount ->
                    TaskListCard(
                        taskList = itemWithCount.taskList,
                        taskCount = itemWithCount.taskCount,
                        onClick = { onListClick(itemWithCount.taskList.listId) },
                        onLongClick = { showActionMenuFor = itemWithCount.taskList }
                    )
                }
            }
        }

        // 1. Меню выбора действий (появляется после долгого нажатия)
        if (showActionMenuFor != null) {
            AlertDialog(
                onDismissRequest = { showActionMenuFor = null },
                title = { Text("Действие со списком") },
                text = { Text("Выберите, что вы хотите сделать со списком «${showActionMenuFor?.name}»") },
                confirmButton = {
                    TextButton(onClick = {
                        listToEdit = showActionMenuFor
                        showActionMenuFor = null
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Изменить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        listPendingDeletion = showActionMenuFor
                        showActionMenuFor = null
                    }) {
                        // Заменяем color на tint
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Удалить", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }

        // 2. Диалог создания
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

        // 3. Диалог редактирования
        if (listToEdit != null) {
            AddItemDialog(
                title = "Редактировать название",
                initialText = listToEdit!!.name,
                onDismiss = { listToEdit = null },
                onConfirm = { newName ->
                    viewModel.updateList(listToEdit!!, newName)
                    listToEdit = null
                }
            )
        }

        // 4. Подтверждение удаления
        if (listPendingDeletion != null) {
            AlertDialog(
                onDismissRequest = { listPendingDeletion = null },
                title = { Text("Удалить список?") },
                text = { Text("Это действие удалит список и все задачи в нем.") },
                confirmButton = {
                    TextButton(onClick = {
                        listPendingDeletion?.let { viewModel.deleteList(it) }
                        listPendingDeletion = null
                    }) {
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
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskListCard(
    taskList: TaskList,
    taskCount: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = taskList.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = when {
                        taskCount == 0 -> "Нет задач"
                        taskCount % 10 == 1 && taskCount % 100 != 11 -> "$taskCount задача"
                        else -> "$taskCount задач"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}