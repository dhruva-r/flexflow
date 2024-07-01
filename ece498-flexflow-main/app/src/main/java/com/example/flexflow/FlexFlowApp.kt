package com.example.flexflow

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.flexflow.ui.settings.SettingsViewModel
import com.example.flexflow.ui.theme.FlexFlowTheme
import dagger.hilt.android.HiltAndroidApp

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlexFlowApp(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by settingsViewModel.uiState.collectAsState()

    FlexFlowTheme(darkTheme = uiState.darkTheme) {
        val navController = rememberNavController()

        //TODO: Finish logic to show bottom nav bar
        val showNavBar = true

        Scaffold(
            bottomBar = {
                if (showNavBar) {
                    FlexFlowNavBar(navController = navController)
                }
            }
        ) { innerPadding ->
            FlexFlowNavGraph(Modifier.padding(innerPadding), navController, startDestination = Screen.Home.route)
        }
    }
}
