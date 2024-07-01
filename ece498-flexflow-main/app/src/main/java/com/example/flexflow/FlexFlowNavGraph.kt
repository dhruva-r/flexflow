package com.example.flexflow

import JournalScreen
import JournalListScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flexflow.ui.schedule.HomeScreen
import com.example.flexflow.ui.theme.SchedScreen
import com.example.flexflow.ui.schedule.AddTaskScreen

import androidx.compose.foundation.layout.Row
import com.example.flexflow.ui.schedule.ViewTasksScreen
import com.example.flexflow.ui.settings.SettingsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlexFlowNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
){
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen()
        }
        composable(
            route = Screen.Tasks.route,
        ) {
            ViewTasksScreen(navController)
        }
        composable(
            route = Screen.AddTasks.route,
        ) {
            AddTaskScreen(onFinish = {navController.navigate(Screen.Tasks.route)},
                onBackClick = {navController.navigate(Screen.Tasks.route)})
        }
        composable(
            route = Screen.AddJournal.route,
            ) {
            JournalScreen(onFinish = {
                navController.navigate(Screen.Journals.route)
            })
        }
        composable(
            route = Screen.Journals.route,
        ) {
            JournalListScreen(navController)
        }
//        composable(
//            route = Screen.Profile.route,
//        ) {
//            SchedScreen()
//        }

        composable(
            route = Screen.Settings.route,
        ) {
            SettingsScreen()
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }

    return hiltViewModel(parentEntry)
}

