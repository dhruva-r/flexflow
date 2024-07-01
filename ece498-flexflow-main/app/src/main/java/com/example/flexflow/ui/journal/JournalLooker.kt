import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.flexflow.Screen
import com.example.flexflow.ui.journal.JournalViewModel
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun JournalListScreen(navController: NavController, journalViewModel: JournalViewModel = hiltViewModel()) {
    val allJournals by journalViewModel.allJournals.collectAsState()

    Column(
        //horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            //.verticalScroll(rememberScrollState(), reverseScrolling = true)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            "Journals",
            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onBackground),
            //modifier = Modifier.padding(4.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Scaffold(
            backgroundColor = MaterialTheme.colorScheme.background,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddJournal.route) },
                    shape = CircleShape
                ) {
                    Icon(Icons.Filled.Add, "floating action button", tint = MaterialTheme.colorScheme.primary)
                }
            }

        ) { innerPadding ->
            LazyColumn(
                Modifier.weight(1f)
            ) {
                items(allJournals) { journal ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                //.clip(RoundedCornerShape(5.dp))
                                .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(4.dp)
                        ) {
                            Column(

                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    //verticalAlignment = Alignment.CenterVertically,
                                    //horizontalArrangement = Arrangement.Center
                                ) {
                                    //if there is no mood?
                                    if (journal.mood == "") {
                                        Text(
                                            text = "\uD83D\uDE00",
                                            fontSize = 24.sp,
                                            modifier = Modifier.padding(
                                                start = 10.dp,
                                                top = 0.dp,
                                                end = 0.dp,
                                                bottom = 0.dp
                                            ).alpha(0.0f)
                                        ) // Emoji on the left
                                    } else {
                                        Text(
                                            text = "${journal.mood}",
                                            fontSize = 24.sp,
                                            modifier = Modifier.padding(
                                                start = 10.dp,
                                                top = 0.dp,
                                                end = 0.dp,
                                                bottom = 0.dp
                                            ),
                                            style = MaterialTheme.typography.bodySmall
                                        ) // Emoji on the left
                                    }
                                    Spacer(modifier = Modifier.width(16.dp)) // Space between emoji and date
                                    val dateFormat: SimpleDateFormat =
                                        SimpleDateFormat("EEEE, MMMM d")
                                    Text(
                                        text = "${dateFormat.format(journal.date)}",
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    ) // Date to the right of the emoji
                                    //                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    //                                val dateFormat: SimpleDateFormat = SimpleDateFormat("EEEE, MMMM d")
                                    //                                Text(text = "${dateFormat.format(journal.date)}", fontSize = 24.sp, textAlign = TextAlign.Center) // Date to the right of the emoji
                                    //
                                    //                            }
                                }
                                Text(text = "${journal.journal_entry}", fontSize = 15.sp, style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp)) // Space between emoji and date
                }
            }
        }
    }
}