package com.example.flexflow.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flexflow.data.entity.EventEntity
import com.example.flexflow.data.room.TaskDao
import com.example.flexflow.data.room.EventDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val eventDao: EventDao
): ViewModel() {
    private val _uiState = MutableStateFlow(ScheduleState())
    val uiState: StateFlow<ScheduleState> = _uiState.asStateFlow();

    init {
        val cal: Calendar = Calendar.getInstance()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                eventDao.getEventsFromDate(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH),
                    cal.get(Calendar.YEAR)).collect {
                    if (it.isNotEmpty()) {
                        _uiState.update { currentState ->
                            currentState.copy(
                                schedule = it
                            )
                        }
                    }

                }
            }
        }
    }

     fun changeDate(newValue: Date) {
        val cal: Calendar = Calendar.getInstance()
        cal.time = newValue
        _uiState.update { currentState ->
            currentState.copy(
                selectedDate = newValue
            )
        }

        CoroutineScope(Dispatchers.IO).launch {
            _uiState.update { currentState ->
                currentState.copy(
                    schedule = eventDao.getEventsFromDateList(cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)),
                )
            }
        }
    }
}
data class ScheduleState(
    val schedule: List<EventEntity> = emptyList(),
    val selectedDate: Date = Date()
)