package com.example.flexflow.ui.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.flexflow.data.entity.TaskEntity
import com.example.flexflow.data.room.EventDao
import com.example.flexflow.data.room.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import com.example.flexflow.ui.schedule.AddEvents

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    val eventDao: EventDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(AddTaskState())
    val uiState: StateFlow<AddTaskState> = _uiState.asStateFlow()

    private val scheduleId: Long? = savedStateHandle["scheduleId"]

    fun updateName(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                name = newValue
            )
        }
    }

    fun updateDetails(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                details = newValue
            )
        }
    }

    fun updatePriority(newValue: Double) {
        _uiState.update { currentState ->
            currentState.copy(
                priority = newValue
            )
        }
    }

    fun updateDeadline(newValue: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                deadline = newValue
            )
        }
    }

    fun updateTimeRestraint(newValue: Double) {
        _uiState.update { currentState ->
            currentState.copy(
                timeRestraint = newValue
            )
        }
    }

    fun updateRequisite(newValue: Double) {
        _uiState.update { currentState ->
            currentState.copy(
                requisite = newValue
            )
        }
    }

    fun updateCommitment(newValue: Double) {
        _uiState.update { currentState ->
            currentState.copy(
                commitment = newValue
            )
        }
    }

    fun updateComplexity(newValue: Double) {
        _uiState.update { currentState ->
            currentState.copy(
                complexity = newValue
            )
        }
    }

    fun updateImportance(newValue: Double) {
        _uiState.update { currentState ->
            currentState.copy(
                importance = newValue
            )
        }
    }

    fun updateDueDate(newValue: Date) {
        _uiState.update { currentState ->
            currentState.copy(
                dueDate = newValue
            )
        }
    }

    fun updateStartDate(newValue: Date) {
        _uiState.update { currentState ->
            currentState.copy(
                startDate = newValue
            )
        }
    }

    fun updateEndDate(newValue: Date) {
        _uiState.update { currentState ->
            currentState.copy(
                endDate = newValue
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun saveTask() {
            CoroutineScope(Dispatchers.IO).launch {
                val timeRestraintWeight = 0.4;
                val importanceWeight = 0.2;
                val requisiteWeight = 0.0;
                val timeCommitmentWeight = 0.2;
                val complexityWeight = 0.2;

                var timeRestraintScore = _uiState.value.commitment * _uiState.value.complexity * 16 / (hoursBetween(_uiState.value.dueDate) - 8 * daysBetween(_uiState.value.endDate))
                if (timeRestraintScore > 1.0) { timeRestraintScore = 1.0}

                var importanceScore = 0.0;
                var requisiteScore = 1.0;   // related to user's mood
                val timeCommitmentScore = _uiState.value.commitment;
                val complexityScore = _uiState.value.complexity;

                val avgPriority = taskDao.getAvgPriorityInBetween(Date(), _uiState.value.dueDate)

                val prePriorityScore = (timeRestraintWeight * timeRestraintScore +
                        requisiteWeight * requisiteScore +
                        timeCommitmentWeight * timeCommitmentScore +
                        complexityWeight * complexityScore) / 0.8

                importanceScore = if (prePriorityScore - avgPriority > 0){
                    0.95
                } else if (prePriorityScore == avgPriority) {
                    0.7
                } else {
                    0.05
                }

                val priorityScore = timeRestraintWeight * timeRestraintScore +
                        importanceWeight * importanceScore +
                        requisiteWeight * requisiteScore +
                        timeCommitmentWeight * timeCommitmentScore +
                        complexityWeight * complexityScore

                val task = TaskEntity(
                    name = _uiState.value.name,
                    details = _uiState.value.details,
                    priority = priorityScore,
                    deadline = _uiState.value.deadline,
                    timeRestraint = timeRestraintScore,
                    requisite = requisiteScore,
                    commitment = timeCommitmentScore,
                    complexity =  complexityScore,
                    importance = importanceScore,
                    dueDate =  _uiState.value.dueDate,
                    startDate = _uiState.value.startDate,
                    endDate = _uiState.value.endDate,

                )
                taskDao.insert(task)
                val eventTask = taskDao.getTaskWithoutId(task.name, task.dueDate, task.priority)
                AddEvents().addEvent(eventTask, eventDao)
                _uiState.update { currentState ->
                    currentState.copy(
                        name = "",
                        details = "",
                        priority= 0.0,
                        deadline= 0,
                        timeRestraint= 0.0,
                        requisite = 0.0,
                        commitment= 0.0,
                        complexity = 0.0,
                        importance = 0.0,
//                        dueDate = Date(0),
//                        startDate = Date(0),
//                        endDate = Date(0)
                    )
                }
            }
    }

    fun changeDateHoursAndMinutes(date: Date, newHour: Int, newMinute: Int): Date {
        // Obtain a Calendar instance and set it to the specified date
        val calendar = Calendar.getInstance().apply {
            time = date
        }

        // Set the new hours and minutes
        calendar.set(Calendar.HOUR_OF_DAY, newHour)
        calendar.set(Calendar.MINUTE, newMinute)

        // Return the updated Date
        return calendar.time
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun isTimePickerStateGreaterThan(firstState: TimePickerState, secondState: TimePickerState): Boolean {
        val firstHour = firstState.hour
        val firstMinute = firstState.minute

        val secondHour = secondState.hour
        val secondMinute = secondState.minute

        return if (firstHour > secondHour) {
            true
        } else if (firstHour == secondHour) {
            firstMinute > secondMinute
        } else {
            false
        }
    }
}

data class AddTaskState(
    val name: String = "",
    val details: String = "",
    val priority: Double = 0.0,
    val deadline: Int = 0,
    val timeRestraint: Double = 0.0,
    val requisite: Double = 0.0,
    val commitment: Double = 0.0,
    val complexity: Double = 0.0,
    val importance: Double = 0.0,
    val dueDate: Date = Date(),
    val startDate: Date = Date(),
    val endDate: Date = Date()
)


@RequiresApi(Build.VERSION_CODES.O)
fun daysBetween(ca: Date): Int{
    val current = Calendar.getInstance()
    val calendar = Calendar.getInstance().apply {
        time = ca
    }
    return ChronoUnit.DAYS.between(current.toInstant(), calendar.toInstant()).toInt()
}

@RequiresApi(Build.VERSION_CODES.O)
fun hoursBetween(end: Date): Int{
    val start = Calendar.getInstance()
    val endCal = Calendar.getInstance().apply {
        time = end
    }
    return ChronoUnit.HOURS.between(start.toInstant(), endCal.toInstant()).toInt()
}
