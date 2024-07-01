package com.example.flexflow.ui.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LabelImportant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flexflow.data.entity.EventEntity
import com.example.flexflow.ui.settings.SettingsViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateNavigation(date: MutableState<LocalDate>, uiState: ScheduleState, homeScreenViewModel: HomeScreenViewModel) {
    //UI for top navigation between the date
    val dateDialogState = rememberMaterialDialogState()
    Row(
        //horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .border(width = 2.dp, color = MaterialTheme.colorScheme.outline)
                .background(color = MaterialTheme.colorScheme.onBackground)
                .padding(4.dp)
                .clickable {
                    date.value = date.value.minusDays(1)
                    homeScreenViewModel.changeDate(
                        Date.from(
                            date.value
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                        )
                    )
                }
        ){
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "MonthBack",
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.background
            )
        }
        Box(
            modifier = Modifier
                //.border(width = 2.dp, color = Color.Black)
                .weight(1f)
                .padding(horizontal = 16.dp)
                .padding(4.dp)
                .clickable {
                    dateDialogState.show()
                },
            contentAlignment = Alignment.Center
        ){
            Text(
                text = DateTimeFormatter.ofPattern("MMMM dd, yyyy").format(date.value),
                textAlign = TextAlign.Center,
                //modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                fontSize = 24.sp
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .border(width = 2.dp, color = MaterialTheme.colorScheme.outline)
                .background(color = MaterialTheme.colorScheme.onBackground)
                .padding(4.dp)
                .clickable {
                    date.value = date.value.plusDays(1)
                    homeScreenViewModel.changeDate(
                        Date.from(
                            date.value
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                        )
                    )
                },
        ){
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "MonthForward",
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.background
            )
        }

        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton(text = "Ok")
                negativeButton(text = "Cancel")
            }
        ){
            datepicker(
                initialDate = LocalDate.now()
            ) {
                date.value = it
                homeScreenViewModel.changeDate(Date.from(date.value.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            }
        }
    }
}

data class Quote(
    val id: Int,
    val quote: String,
    val name: String
)

val single_quote = Quote(
    1,
    "Fortune, which has a great deal of power in other matters but especially in war, can bring about great changes in a situation through very slight forces.",
    "Julius Caesar"
    )

//consider option to disable daily quote in user settings
@Composable
fun QuoteItem(show_quote_flag: Boolean, quote: Quote) {
    if(show_quote_flag){
        Text(text = "Quote of the Day",
            textAlign = TextAlign.Left,
            modifier = Modifier.padding(4.dp),
            fontSize = 20.sp,
            style = MaterialTheme.typography.bodySmall
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )

        ) {
            Text(text = "${quote.quote}\n" +
                    "- ${quote.name}",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }else{

    }
}
data class Event(
    val name: String,
    val details: String,
    val startTime: Date,
    val endTime: Date,
    val taskId: Int,
    val priority: Double,
    val eventCompletion: Boolean,
    val taskCompletion: Boolean,
    val day: Int,
    val month: Int,
    val year: Int
)


@Composable
fun EventItem(event: EventEntity) {
    val dateFormat: SimpleDateFormat = SimpleDateFormat("hh:mm a")
    Text(
        text = "${dateFormat.format(event.startDate)} - ${dateFormat.format(event.endDate)}",
        textAlign = TextAlign.Left,
        modifier = Modifier.padding(2.dp),
        fontSize = 16.sp,
        style = MaterialTheme.typography.labelSmall
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){

        Box(
            modifier = Modifier
                //.clip(RoundedCornerShape(5.dp))
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(4.dp)

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement  =  Arrangement.SpaceBetween
            ) {
                Column(

                ){
                    Text(
                        text = event.name,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.padding(2.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = event.details,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.padding(2.dp),
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if(event.priority == 1.0){
                    Icon(
                        imageVector = Icons.Default.LabelImportant,
                        contentDescription = "important",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

    }
}

@Composable
fun DayView(events: List<EventEntity>) {
    Text(text = "Today's Schedule",
        textAlign = TextAlign.Left,
        modifier = Modifier.padding(4.dp),
        fontSize = 20.sp,
        style = MaterialTheme.typography.headlineMedium
    )
    //if nothing in list, say "Nothing is scheduled :)"
    LazyColumn {
        items(events) { event ->
            EventItem(event)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}

) {
    val uiState by homeScreenViewModel.uiState.collectAsState()
    val uiStateSettings by settingsViewModel.uiState.collectAsState()
    var date = remember { mutableStateOf(LocalDate.now().plusDays(0)) }

    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                //.verticalScroll(rememberScrollState(), reverseScrolling = true)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            //UI for top navigation between months
            DateNavigation(date, uiState, homeScreenViewModel)


            Spacer(modifier = Modifier.height(4.dp))

            //UI For the motivational message of the month
            //check if user setting has this enabled or disabled
            //retrieve today's quote using today's date
            QuoteItem(uiStateSettings.showDailyQuote, single_quote)

            //UI for the day view
            DayView(events = uiState.schedule)
        }
    }
}