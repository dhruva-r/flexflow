package com.example.flexflow.ui.journal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flexflow.data.entity.EventEntity
import com.example.flexflow.data.entity.JournalEntity
import com.example.flexflow.data.room.EventDao
import com.example.flexflow.data.room.JournalDao
import com.example.flexflow.ui.schedule.AddEvents
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalDao: JournalDao,
    val eventDao: EventDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(AddJournalState())
    val uiState: StateFlow<AddJournalState> = _uiState.asStateFlow()
    var result = MutableStateFlow("")
    val allJournals: StateFlow<List<JournalEntity>> = journalDao.getAllJournals().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val emojis_dict = mapOf("sadness" to "😢", "anger" to "😡", "fear" to "😨", "joy" to "😀", "love" to "🥰", "surprise" to "😲")
    // Dont really need this since Flow auto updates UI elements when database is updated
    //fun refreshJournals() {
    //}
    fun findKeyWithMaxValue(jsonObj: JSONObject): String? {
        var maxKey: String? = null
        var maxValue = -1.0 // Initialize with a value that's lower than any possible score


        for (key in iterate(jsonObj.keys())) {
            val value: Double = jsonObj.getDouble(key)
            if (value > maxValue) {
                maxValue = value
                maxKey = key
            }
        }

        return maxKey
    }
    fun updateJournalEntry(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                journal_entry = newValue
            )
        }
    }

    fun updateMood(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                mood = newValue
            )
        }
    }
    val client = OkHttpClient.Builder()
        .callTimeout(1, TimeUnit.MINUTES) // Total timeout for the entire operation
        .connectTimeout(30, TimeUnit.SECONDS) // Time allowed to establish initial connection
        .readTimeout(30, TimeUnit.SECONDS) // Time allowed for reading data from the server
        .writeTimeout(30, TimeUnit.SECONDS) // Time allowed for sending data to the server
        .build()
    val JSON = "application/json; charset=utf-8".toMediaType()
    fun saveJournal() {
        CoroutineScope(Dispatchers.IO).launch {
            val current = Calendar.getInstance()
            val journal = JournalEntity(
                journal_entry = _uiState.value.journal_entry,
                mood = _uiState.value.mood,
                date = current.time
            )
            println(_uiState.value.journal_entry)
            println("test printout")
            val request = Request.Builder()
                .url("https://nt8szr9vcl.execute-api.us-east-1.amazonaws.com/dev/classify")
                .post(_uiState.value.journal_entry.toRequestBody(JSON))
                .build()

            var exercise = ""

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                var answer = JSONObject(response.body.string())
                exercise = answer.getString("journal")
                val moods = JSONObject(answer["moods"].toString())
                println(answer["journal"])
                //result.update{str -> answer}
                journal.journal_entry += "\n\n" + "Recommended exercise: "+ exercise.substring(1, exercise.length-1) + "\n\n" + "Mood: "+emojis_dict[findKeyWithMaxValue(moods)]
            }
            journalDao.insert(journal)

            val calendar = Calendar.getInstance()
            calendar.time = current.time
            calendar.add(Calendar.DATE, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 8)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)


            val calendarEnd = Calendar.getInstance()
            calendarEnd.time = calendar.time
            calendarEnd.add(Calendar.MINUTE, 30)

            println("calendar: $calendar \n calendarEnd: $calendarEnd")

            val finalEvent = EventEntity(
                name = exercise.substring(1, exercise.length-1),
                details = "",
                startDate = calendar.time,
                endDate = calendarEnd.time,
                taskId = 0,
                priority = 0.5,
                eventCompletion = false,
                taskCompletion = false,
                day = calendar.get(Calendar.DAY_OF_MONTH),
                month = calendar.get(Calendar.MONTH),
                year = calendar.get(Calendar.YEAR)
            )

            eventDao.insert(finalEvent)

            val updatedEvents = eventDao.getEventsFromDateListPrio(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)
            )

            AddEvents().createSchedule(updatedEvents, eventDao)

            _uiState.update { currentState ->
                currentState.copy(
                    journal_entry = "",
                    mood = "",
                    date = Date(0)
                )
            }
        }
    }
}

data class AddJournalState(
    val journal_entry: String = "",
    val mood: String = "",
    val date: Date = Date(0)
)

private fun <T> iterate(i: Iterator<T>): Iterable<T> {
    return object : Iterable<T> {
        override fun iterator(): Iterator<T> {
            return i
        }
    }
}