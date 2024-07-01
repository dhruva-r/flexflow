package com.example.flexflow.ui.schedule

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.flexflow.Screen
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ViewTasksScreen(navController: NavController, viewTasksViewModel: ViewTasksViewModel = hiltViewModel()) {
    val allTasks by viewTasksViewModel.allTasks.collectAsState()

    Column(
        //horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            //.verticalScroll(rememberScrollState(), reverseScrolling = true)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            "Tasks",
            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            //modifier = Modifier.padding(4.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Scaffold(
            backgroundColor = MaterialTheme.colorScheme.background,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddTasks.route) },
                    shape = CircleShape
                ) {
                    Icon(Icons.Filled.Add, "floating action button", tint = MaterialTheme.colorScheme.primary)

                }
            }

        ) { innerPadding ->
            LazyColumn(
                Modifier
                    .weight(1f)
                    .background(color = MaterialTheme.colorScheme.background)

            ) {
                items(allTasks) { task ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
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
                                .padding(5.dp)
                        ) {
                            Column(

                            ) {
                                Text(text = "${task.name}", fontSize = 20.sp, style= MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    //verticalAlignment = Alignment.CenterVertically,
                                    //horizontalArrangement = Arrangement.Center
                                ) {
                                    val dateFormat: SimpleDateFormat =
                                        SimpleDateFormat("EEEE, MMMM d")
                                    Text(
                                        text = "Due: ${dateFormat.format(task.dueDate)}",
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center,
                                        style= MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                Text(text = "Task Priority: ${String.format("%.3f", task.priority)}", fontSize = 15.sp, style= MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "Estimated Commitment: ${task.commitment*4}", fontSize = 15.sp, style= MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "Estimated Complexity: ${task.complexity*4}", fontSize = 15.sp, style= MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            }
                                Button(onClick = { viewTasksViewModel.deleteTask(task.id)},
                                        modifier = Modifier.align(Alignment.BottomEnd)) {
                                    Text(text = "Delete", color = MaterialTheme.colorScheme.surface)
                                }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}