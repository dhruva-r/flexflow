package com.example.flexflow.ui.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flexflow.rememberTextClassifier
import org.tensorflow.lite.support.label.Category
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

/* references:
    - datepicker: https://semicolonspace.com/jetpack-compose-date-picker-material3/
    - timepicker: https://semicolonspace.com/jetpack-compose-time-picker-material3/
 */

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    addTaskViewModel: AddTaskViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onFinish: () -> Unit
) {
    val uiState by addTaskViewModel.uiState.collectAsState()
    val timeClassifier = rememberTextClassifier(modelPath = "time.tflite")
    val complexClassifier = rememberTextClassifier(modelPath = "complex.tflite")
    //var localContext = LocalContext.current
    var results by remember { mutableStateOf<List<Category>>(emptyList()) }
    var results2 by remember { mutableStateOf<List<Category>>(emptyList()) }

    val calendar = Calendar.getInstance()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)

    var analyzed by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }
    var selectedDateEnd by remember { mutableStateOf(calendar.timeInMillis) }

    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedHourEnd by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }
    var selectedMinuteEnd by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showTimePickerEnd by remember { mutableStateOf(false) }
    val timePickerState =
        rememberTimePickerState(initialHour = selectedHour, initialMinute = selectedMinute)
    val timePickerStateEnd =
        rememberTimePickerState(initialHour = selectedHourEnd, initialMinute = selectedMinuteEnd)

    val dueTime = Instant.ofEpochMilli(selectedDate)
    val endTime = Instant.ofEpochMilli(selectedDateEnd)
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    if (showDatePicker) {
        DatePickerDialog(onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    selectedDate = datePickerState.selectedDateMillis!!
                    selectedDateEnd = datePickerState.selectedDateMillis!!
                    addTaskViewModel.updateDueDate(Date(selectedDate))
                    addTaskViewModel.updateStartDate(Date(selectedDate))
                }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = "Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    if (showTimePicker) {
        AlertDialog(onDismissRequest = { showTimePicker = false }) {
            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                    )
                    .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // time picker
                TimePicker(state = timePickerState)

                // buttons
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // dismiss button
                    TextButton(onClick = { showTimePicker = false }) {
                        Text(text = "Dismiss")
                    }

                    // confirm button
                    TextButton(
                        onClick = {
                            showTimePicker = false
                            selectedHour = timePickerState.hour
                            selectedMinute = timePickerState.minute
                            selectedDate = addTaskViewModel.changeDateHoursAndMinutes(
                                Date(selectedDate),
                                selectedHour,
                                selectedMinute
                            ).time
                            addTaskViewModel.updateDueDate(Date(selectedDate))
                            addTaskViewModel.updateStartDate(Date(selectedDate))
                        }
                    ) {
                        Text(text = "Confirm")
                    }
                }
            }
        }
    }

    if (showTimePickerEnd) {
        AlertDialog(onDismissRequest = { showTimePickerEnd = false }) {
            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                    )
                    .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // time picker
                TimePicker(state = timePickerStateEnd)

                // buttons
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // dismiss button
                    TextButton(onClick = { showTimePickerEnd = false }) {
                        Text(text = "Dismiss")
                    }

                    // confirm button
                    TextButton(
                        onClick = {
                            showTimePickerEnd = false

                            if (addTaskViewModel.isTimePickerStateGreaterThan(
                                    timePickerStateEnd,
                                    timePickerState
                                )
                            ) {
                                selectedHourEnd = timePickerStateEnd.hour
                                selectedMinuteEnd = timePickerStateEnd.minute
                                selectedDateEnd = addTaskViewModel.changeDateHoursAndMinutes(
                                    Date(selectedDateEnd),
                                    selectedHourEnd,
                                    selectedMinuteEnd
                                ).time
                            } else {
                                selectedHourEnd = timePickerState.hour
                                selectedMinuteEnd = timePickerState.minute
                                selectedDateEnd = addTaskViewModel.changeDateHoursAndMinutes(
                                    Date(selectedDateEnd),
                                    selectedHourEnd,
                                    selectedMinuteEnd
                                ).time

                            }
                            addTaskViewModel.updateEndDate(Date(selectedDateEnd))
                        }
                    ) {
                        Text(text = "Confirm")
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState(), reverseScrolling = true)
            .padding(horizontal = 30.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onBackClick() },
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }

            IconButton(
                onClick = { addTaskViewModel.saveTask()
                          analyzed = false
                          onFinish()},
                modifier = Modifier
                    .background(
                        color = if (analyzed && uiState.name.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    ),
                enabled = analyzed && uiState.name.isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Add Tasks",
            modifier = Modifier.align(Alignment.Start),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.name,
            onValueChange = {
                addTaskViewModel.updateName(it)
            },
            label = { Text("Task Name", color = Color.DarkGray) },
            placeholder = {
                Text(
                    "Enter your task here.",
                    color = Color.LightGray
                )
            },
            singleLine = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )

        OutlinedTextField(
            value = uiState.details,
            onValueChange = {
                addTaskViewModel.updateDetails(it)
            },
            label = { Text("Description, e.g. Location", color = Color.DarkGray) },
            placeholder = {
                Text(
                    "Details about your task.",
                    color = Color.LightGray
                )
            },
            singleLine = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp)),
            onClick = {
                timeClassifier.detectSentiment(uiState.name) { time ->
                    results = time
                    val x = results.indices.maxBy { results[it].score } ?: -1
                    addTaskViewModel.updateCommitment((x + 1) / 4.0);
                }

                complexClassifier.detectSentiment(uiState.name) { complex ->
                    results2 = complex
                    val y = results2.indices.maxBy { results2[it].score } ?: -1
                    addTaskViewModel.updateComplexity((y + 1) / 4.0)
                }

                analyzed = true

            },
            enabled = uiState.name.isNotEmpty()
        ) {
            Icon(
                imageVector = Icons.Default.Sensors,
                contentDescription = "Analyze",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text("Analyze", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        contentDescription = "Edit Due Date",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Edit Start Time",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = { showTimePickerEnd = true },
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Update,
                        contentDescription = "Edit End Time",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Due Date",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateFormatter.format(dueTime.atZone(ZoneId.systemDefault()))
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Start Time",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = timeFormatter.format(dueTime.atZone(ZoneId.systemDefault()))
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "End Time",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = timeFormatter.format(endTime.atZone(ZoneId.systemDefault()))
                )
            }
        }
    }
}










