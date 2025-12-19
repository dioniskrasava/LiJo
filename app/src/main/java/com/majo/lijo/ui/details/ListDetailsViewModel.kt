package com.majo.lijo.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.majo.lijo.data.local.entities.ListItem
import com.majo.lijo.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel для экрана деталей конкретного списка.
 * * @property repository Репозиторий для работы с данными задач и списков.
 * @property savedStateHandle Хранилище состояния, из которого мы получаем [listId], переданный при навигации.
 */
@HiltViewModel
class ListDetailsViewModel @Inject constructor(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Извлекаем ID списка из аргументов навигации
    private val listId: Long = checkNotNull(savedStateHandle["listId"])

    /**
     * Поток с названием текущего списка.
     * Мы берем все списки из репозитория и фильтруем тот, чей ID совпадает с нашим.
     */
    val listName: StateFlow<String> = repository.allLists
        .map { lists ->
            lists.find { it.listId == listId }?.name ?: "Загрузка..."
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Список дел"
        )

    /**
     * Поток элементов (задач) внутри данного списка.
     * Обновляется автоматически при изменении данных в БД Room.
     */
    val items: StateFlow<List<ListItem>> = repository.getItems(listId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Добавление новой задачи в текущий список.
     */
    fun addItem(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addItem(listId, title)
        }
    }

    /**
     * Переключение статуса выполнения задачи (выполнено/не выполнено).
     */
    fun onCheckedChange(item: ListItem) {
        viewModelScope.launch {
            repository.toggleItemCompletion(item)
        }
    }
}