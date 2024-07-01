package com.example.flexflow.ui.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flexflow.data.entity.UserEntity
import com.example.flexflow.data.room.UserDao
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
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDao: UserDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val entity = userDao.getUserById(1)
                if(entity != null){
                    _uiState.update { currentState ->
                        currentState.copy(
                            showDailyQuote = entity.showDailyQuote,
                            darkTheme = entity.darkTheme
                        )
                    }
                }else{
                    _uiState.update { currentState ->
                        currentState.copy(
                            showDailyQuote = true,
                            darkTheme = false
                        )
                    }
                }
            }
        }
    }

    fun saveSettings(showDailyQuote: Boolean, darkTheme: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val settings = UserEntity(
                id = 1,
                showDailyQuote = showDailyQuote,
                darkTheme = darkTheme
            )
            val entity = userDao.getUserById(1)
            if(entity != null){
                userDao.update(settings)
            }else{
                userDao.insert(settings)
            }
            _uiState.update { currentState ->
                currentState.copy(
                    showDailyQuote = showDailyQuote,
                    darkTheme = darkTheme
                )
            }
        }
    }
}

data class SettingsState(
    val showDailyQuote: Boolean = true,
    val darkTheme: Boolean = false
)