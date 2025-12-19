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
import androidx.hilt.navigation.compose.hiltViewModel // Важно для hiltViewModel()
import androidx.compose.foundation.ExperimentalFoundationApi // Для animateItemPlacement
import androidx.compose.animation.core.tween // Для анимации
import com.majo.lijo.data.local.entities.TaskList
import com.majo.lijo.ui.components.AddItemDialog

import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onListClick: (Long) -> Unit, // Callback для навигации
    onSettingsClick: () -> Unit
) {
    val lists by viewModel.lists.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    // Состояние для открытия/закрытия меню
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои списки") },
                actions = { // 2. Добавляем секцию действий
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
                                onSettingsClick() // Переход в настройки
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
                items(lists, key = { it.listId }) { list ->
                    TaskListCard(
                        taskList = list,
                        onClick = { onListClick(list.listId) },
                        onDelete = { viewModel.deleteList(list) }
                    )
                }
            }
        }

        if (showCreateDialog) {
            // Предполагаем наличие простого диалога ввода текста
            AddItemDialog(
                title = "Новый список",
                onDismiss = { showCreateDialog = false },
                onConfirm = { name ->
                    viewModel.createList(name)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun TaskListCard(
    taskList: TaskList,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = taskList.name, style = MaterialTheme.typography.titleMedium)
                // Можно добавить дату создания
                Text(
                    text = "ID: ${taskList.listId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}