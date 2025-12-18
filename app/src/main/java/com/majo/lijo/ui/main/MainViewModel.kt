package com.majo.lijo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.majo.lijo.data.local.entities.TaskList
import com.majo.lijo.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlin.collections.emptyList // ???


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    val lists = repository.allLists
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList<TaskList>()
        )

    fun createList(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.createList(name)
        }
    }

    fun deleteList(taskList: TaskList) {
        viewModelScope.launch {
            repository.deleteList(taskList)
        }
    }
}