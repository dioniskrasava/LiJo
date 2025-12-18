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
import kotlinx.coroutines.flow.stateIn


@HiltViewModel
class ListDetailsViewModel @Inject constructor(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle // Для получения аргументов навигации
) : ViewModel() {

    private val listId: Long = checkNotNull(savedStateHandle["listId"]) // Аргумент из Navigation

    // StateFlow, который UI будет наблюдать.
    // Room возвращает Flow, мы конвертируем его в StateFlow.
    val items: StateFlow<List<ListItem>> = repository.getItems(listId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addItem(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addItem(listId, title)
        }
    }

    fun onCheckedChange(item: ListItem) {
        viewModelScope.launch {
            // Вся магия перемещения "вниз" происходит внутри репозитория/DAO
            repository.toggleItemCompletion(item)
        }
    }
}