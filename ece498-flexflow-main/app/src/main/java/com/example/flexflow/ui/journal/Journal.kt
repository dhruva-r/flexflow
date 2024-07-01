import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flexflow.data.entity.JournalEntity

// Might not need this import cus class is in same directory


import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.example.flexflow.ui.journal.JournalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    //viewModel: JournalViewModel,
    journalViewModel: JournalViewModel = hiltViewModel(),
    onFinish: () -> Unit = {}
) {
    val uiState by journalViewModel.uiState.collectAsState()
    val textState = remember { mutableStateOf("") }
    val moodState = remember { mutableStateOf("") }
    val selectedMood = remember { mutableStateOf("") }

//    val colors = if (isSystemInDarkTheme()) {
//        darkColors(
//            primary = Color.Blue,
//            onPrimary = Color.White,
//            surface = Color.DarkGray,
//            onSurface = Color.White,
//            background = Color.Black, // Black for dark theme background
//            onBackground = Color.White // White text on black background
//        )
//    } else {
//        lightColors(
//            primary = Color.Blue,
//            onPrimary = Color.White,
//            surface = Color.LightGray,
//            onSurface = Color.Black,
//            background = Color.White, // White for light theme background
//            onBackground = Color.Black // Black text on white background
//        )
//    }

    //MaterialTheme(colors = colors) {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            Text(
                "Add Journal Entry",
                style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier.padding(14.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(50.dp))
                OutlinedTextField(
                    value = textState.value,
                    onValueChange = { textState.value = it
                        journalViewModel.updateJournalEntry(textState.value);},
                    label = {
                        Text(
                            "Enter your thoughts here",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier
                        .fillMaxWidth(0.94f)
                        //.fillMaxHeight(0.7f),
                        .fillMaxHeight(0.5f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Mood",
                    style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onBackground)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val moods = mapOf("😀" to "Happy", "😐" to "Neutral", "😕" to "Unhappy", "😢" to "Sad", "😡" to "Angry")
                    moods.forEach { (mood, moodText) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(start = 10.dp, top = 0.dp, end = 10.dp, bottom = 0.dp)) {
                            IconButton(onClick = {
                                moodState.value = mood
                                selectedMood.value = mood
                            }) {
                                Text(
                                    mood,
                                    style = TextStyle(fontSize = 25.sp),
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .background(if (selectedMood.value == mood) Color.LightGray else Color.Transparent)
                                )
                            }
                            Text(
                                moodText,
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .border(width = 2.dp, color = MaterialTheme.colorScheme.primary)
                        .background(color = MaterialTheme.colorScheme.primary)
                        .padding(4.dp)
                        .clickable {
                            //Update the emoji mood that was selected
                            journalViewModel.updateMood(moodState.value);

                            // Save the entries to the database
                            journalViewModel.saveJournal()

                            textState.value = ""
                            moodState.value = ""
                            selectedMood.value = ""

                            onFinish()
                        },
                ){
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "MonthForward",
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.background
                    )
                }


            }
        }
    //}
}