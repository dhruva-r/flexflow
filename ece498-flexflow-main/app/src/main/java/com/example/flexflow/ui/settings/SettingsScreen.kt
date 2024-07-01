package com.example.flexflow.ui.settings

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flexflow.ui.auth.GoogleLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

@Preview(showBackground = true)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by settingsViewModel.uiState.collectAsState()

    val firebaseAuth = FirebaseAuth.getInstance()
    val composableScope = rememberCoroutineScope();
    val context = LocalContext.current

    val databasePath = context.getDatabasePath("flexflow-db")
    val walPath = context.getDatabasePath("flexflow-db-wal")
    val shmPath = context.getDatabasePath("flexflow-db-shm")

    val databaseURIs = listOf<File>(databasePath, walPath, shmPath)

    val userId = firebaseAuth.currentUser?.uid

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Login()
        Spacer(modifier = Modifier.height(10.dp))
        ProfileSettings(uiState, settingsViewModel)
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                composableScope.launch {
                    backupRoom(context, databaseURIs, userId)
                }
            }) {
                Text("Backup")
            }

            Spacer(Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = {
                    composableScope.launch {
                        restoreRoom(context, databaseURIs, userId)
                    }
                }) {
                    Text("Restore")
                }

                Text("Requires Restart", Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
fun Login(

) {
    GoogleLogin()
}

//show daily quote
//change theme
//delete all
//delete all confirmation
@Composable
fun ProfileSettings(
    uiState: SettingsState,
    settingsViewModel: SettingsViewModel
) {

    Text(
        "Show Daily Quote",
        textAlign = TextAlign.Left,
        modifier = Modifier.padding(4.dp),
        fontSize = 16.sp
    )
    Switch(
        modifier = Modifier.semantics { contentDescription = "toggleQuote" },
        checked = uiState.showDailyQuote,
        onCheckedChange = {
            settingsViewModel.saveSettings(
                !uiState.showDailyQuote,
                uiState.darkTheme
            )
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    )

    Text(
        "Dark Theme (Requires Restart)",
        textAlign = TextAlign.Left,
        modifier = Modifier.padding(4.dp),
        fontSize = 16.sp
    )
    Switch(
        modifier = Modifier.semantics { contentDescription = "toggleTheme" },
        checked = uiState.darkTheme,
        onCheckedChange = {
            settingsViewModel.saveSettings(
                uiState.showDailyQuote,
                !uiState.darkTheme
            )
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    )
}

suspend fun backupRoom(context: Context, databaseUri: List<File>, uid: String?) {
    val storageRef = FirebaseStorage.getInstance().reference
    CoroutineScope(Dispatchers.IO).launch {
        (databaseUri.indices).map { index ->
            async(Dispatchers.IO) {
                val file = Uri.fromFile(File(databaseUri[index].absolutePath))
                val fileName = databaseUri[index].name
                storageRef.child("backups/encrypted/$fileName")
                    .putFile(file)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Backup success: $fileName", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener {
                        println("upload fail: $index")
                    }.await()
            }
        }
    }
}

suspend fun restoreRoom(context: Context, databaseUri: List<File>, uid: String?) {
    val storageRef = FirebaseStorage.getInstance().reference
    CoroutineScope(Dispatchers.IO).launch {
        (databaseUri.indices).map { index ->
            async(Dispatchers.IO) {
                val file = File(databaseUri[index].absolutePath)
                val localFile = File.createTempFile("tempDb", "db")
                val fileName = databaseUri[index].name
                storageRef.child("backups/encrypted/$fileName")
                    .getFile(localFile)
                    .addOnSuccessListener {
                        localFile.copyTo(file, overwrite = true)
                        Toast.makeText(context, "Restore success: $fileName", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener {
                        println("restore fail: $index")
                    }.await()
            }
        }
    }
}
