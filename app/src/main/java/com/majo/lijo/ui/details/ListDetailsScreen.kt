package com.majo.lijo.ui.details

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel // Важно для hiltViewModel()
import androidx.compose.foundation.ExperimentalFoundationApi // Для animateItemPlacement
import androidx.compose.animation.core.tween // Для анимации
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import com.majo.lijo.ui.components.AddItemDialog
import com.majo.lijo.ui.components.TaskItemCard

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class) // Для animateItemPlacement
@Composable
fun ListDetailsScreen(
    viewModel: ListDetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    var showAddItemDialog by remember { mutableStateOf(false) } // Простейшая реализация диалога

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Список дел") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddItemDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp) // Отступ под FAB
        ) {
            items(
                items = items,
                key = { it.itemId } // КРИТИЧНО ВАЖНО для анимации!
            ) { item ->

                // Swipe to Delete можно обернуть здесь в SwipeToDismissBox

                TaskItemCard(
                    item = item,
                    onCheckedChange = { viewModel.onCheckedChange(item) },
                    onDeleteClick = { /* logic */ },
                    modifier = Modifier.animateItemPlacement( // <-- ИСПОЛЬЗУЕМ ЭТО
                        animationSpec = tween(durationMillis = 500)
                    )
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