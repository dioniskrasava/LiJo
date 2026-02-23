package com.majo.lijo.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown

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

    // Состояния для отслеживания перетаскиваемого элемента и его смешения
    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }
    val lazyListState = rememberLazyListState()

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
                state = lazyListState,
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(lists, key = { index, item -> item.taskList.listId }) { index, item ->
                    val modifier = if (draggingIndex == index) {
                        Modifier.offset(y = dragOffset.dp)
                    } else Modifier

                    TaskListCard(
                        taskList = item.taskList,
                        taskCount = item.taskCount,
                        onClick = { onListClick(item.taskList.listId) },
                        onLongClick = { showActionMenuFor = item.taskList },
                        modifier = modifier.draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                dragOffset += delta
                                // Здесь можно вычислять целевой индекс на основе смещения и обновлять список
                            },
                            onDragStarted = {
                                draggingIndex = index
                            },
                            onDragStopped = {
                                // Завершение перетаскивания: обновить позиции в БД
                                draggingIndex?.let { startIndex ->
                                    // Вычислить новый индекс и обновить lists
                                    val newLists = lists.toMutableList()
                                    // ... логика перестановки
                                    viewModel.reorderLists(newLists)
                                }
                                draggingIndex = null
                                dragOffset = 0f
                            }
                        )
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
            ListDialog(
                title = "Новый список",
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, color, icon, _ ->
                    viewModel.createList(name, color, icon)
                    showCreateDialog = false
                }
            )
        }

        // 3. Диалог редактирования
        if (listToEdit != null) {
            val currentPosition = listToEdit!!.position
            val maxPosition = lists.size - 1
            ListDialog(
                title = "Редактировать список",
                initialName = listToEdit!!.name,
                initialColor = listToEdit!!.color,
                initialIcon = listToEdit!!.icon,
                initialPosition = currentPosition,
                maxPosition = maxPosition,
                onDismiss = { listToEdit = null },
                onConfirm = { newName, color, icon, newPosition ->
                    // Обновляем данные самого списка
                    val updatedTaskList = listToEdit!!.copy(
                        name = newName,
                        color = color,
                        icon = icon
                    )
                    // Находим соответствующий элемент в lists
                    val oldItemWithCount = lists.find { it.taskList.listId == listToEdit!!.listId }!!
                    val newItemWithCount = oldItemWithCount.copy(taskList = updatedTaskList)

                    // Создаём копию списка и меняем порядок
                    val mutableLists = lists.toMutableList()
                    mutableLists.removeAt(currentPosition)
                    mutableLists.add(newPosition, newItemWithCount)

                    // Вызываем reorderLists с новым порядком (список TaskListWithCount)
                    viewModel.reorderLists(mutableLists)
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
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
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
            // Кружок с иконкой и цветом
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = taskList.color?.let { Color(it) } ?: MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconFromName(taskList.icon),
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Spacer(Modifier.width(16.dp))
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

// Вспомогательная функция для получения иконки по имени
fun getIconFromName(iconName: String?): ImageVector {
    return when (iconName) {
        "Work" -> Icons.Default.Work
        "Home" -> Icons.Default.Home
        "Shopping" -> Icons.Default.ShoppingCart
        "Favorite" -> Icons.Default.Favorite
        "Star" -> Icons.Default.Star
        else -> Icons.Default.List
    }
}



// ВЫНЕСТИ В БУДУЩЕМ В ОТДЕЛЬНЫЙ КОМПОНЕНТ!!!!

val availableColors = listOf(
    Color(0xFFF44336) to 0xFFF44336, // Красный
    Color(0xFF4CAF50) to 0xFF4CAF50, // Зелёный
    Color(0xFF2196F3) to 0xFF2196F3, // Синий
    Color(0xFFFFC107) to 0xFFFFC107, // Жёлтый
    Color(0xFF9C27B0) to 0xFF9C27B0, // Фиолетовый
    Color(0xFFFF9800) to 0xFFFF9800, // Оранжевый
)

val availableIcons = listOf(
    Icons.Default.Work to "Work",
    Icons.Default.Home to "Home",
    Icons.Default.ShoppingCart to "Shopping",
    Icons.Default.Favorite to "Favorite",
    Icons.Default.Star to "Star",
    Icons.Default.List to "List"  // иконка по умолчанию
)

@Composable
fun ListDialog(
    title: String = "Новый список",
    initialName: String = "",
    initialColor: Long? = null,
    initialIcon: String? = null,
    initialPosition: Int = 0,
    maxPosition: Int = 0,
    onDismiss: () -> Unit,
    onConfirm: (String, Long?, String?, Int) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedColor by remember { mutableStateOf(initialColor) }
    var selectedIcon by remember { mutableStateOf(initialIcon) }
    var position by remember { mutableStateOf(initialPosition) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название списка") },
                    singleLine = true
                )
                Spacer(Modifier.height(16.dp))

                // поле позиции (только для редактирования)
                if (title.contains("Редактировать")) { // или передавать флаг isEditing
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Позиция", modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { if (position > 0) position-- },
                            enabled = position > 0
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Выше")
                        }
                        Text("${position + 1}", modifier = Modifier.padding(horizontal = 8.dp))
                        IconButton(
                            onClick = { if (position < maxPosition) position++ },
                            enabled = position < maxPosition
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Ниже")
                        }
                    }
                }

                Text("Цвет", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableColors.forEach { (color, colorLong) ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColor = colorLong }
                                .then(
                                    if (selectedColor == colorLong)
                                        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    else Modifier
                                )
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Иконка", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableIcons.forEach { (icon, iconName) ->
                        IconButton(
                            onClick = { selectedIcon = iconName },
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    if (selectedIcon == iconName) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else Color.Transparent,
                                    CircleShape
                                )
                        ) {
                            Icon(icon, contentDescription = iconName)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, selectedColor, selectedIcon, position)
                    }
                }
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}