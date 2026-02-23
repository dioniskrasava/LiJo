package com.majo.lijo.ui.details

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import com.majo.lijo.ui.components.AddItemDialog
import com.majo.lijo.ui.components.TaskItemCard
//import androidx.compose.foundation.lazy.animateItemPlacement
import com.majo.lijo.data.local.entities.ListItem

/**
 * Экран деталей списка задач.
 * * @param viewModel Внедряется через Hilt.
 * @param onBackClick Колбэк для возврата на предыдущий экран.
 */
@OptIn( ExperimentalMaterial3Api::class)
@Composable
fun ListDetailsScreen(
    viewModel: ListDetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    // Подписка на состояние элементов и названия списка
    val items by viewModel.items.collectAsState()
    val title by viewModel.listName.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                // ТЕПЕРЬ ЗАГОЛОВОК ДИНАМИЧЕСКИЙ
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddItemDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить задачу")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(
                items = items,
                key = { it.itemId } // Важно для корректной работы анимаций перемещения
            ) { item: ListItem ->
                TaskItemCard(
                    item = item,
                    onCheckedChange = { viewModel.onCheckedChange(item) },
                    onDeleteClick = { /* Здесь можно добавить удаление задачи */ },
                    modifier = Modifier.animateItem()
                )
            }
        }

        if (showAddItemDialog) {
            AddItemDialog(
                onDismiss = { showAddItemDialog = false },
                onConfirm = { text ->
                    viewModel.addItem(text)
                    showAddItemDialog = false
                }
            )
        }
    }
}