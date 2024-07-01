package com.example.flexflow.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flexflow.data.entity.JournalEntity
import com.example.flexflow.data.entity.TaskEntity
import com.example.flexflow.data.room.EventDao
import com.example.flexflow.data.room.JournalDao
import com.example.flexflow.data.room.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ViewTasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    val eventDao: EventDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(ViewTaskState())
    val uiState: StateFlow<ViewTaskState> = _uiState.asStateFlow()
    val allTasks: StateFlow<List<TaskEntity>> =
        taskDao.getAllTasks().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun deleteTask(taskId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val events = eventDao.getEventsFromTaskId(taskId)
            for(event in events){
                eventDao.deleteById(event.id)
                val x = eventDao.getEventsFromDateList(event.day, event.month, event.year)
                if(x.isNotEmpty()){
                    AddEvents().createSchedule(x, eventDao)
                }
            }
            taskDao.deleteById(taskId)
        }
    }
}

data class ViewTaskState(
    val journal_entry: String = "",
    val mood: String = "",
    val date: Date = Date(0)
)
