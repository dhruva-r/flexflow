//package com.example.flexflow.ui.theme
//
//import CalendarScreen
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import com.example.flexflow.ui.AddTaskScreen
//import androidx.hilt.navigation.compose.hiltViewModel
//
//
//
//@Composable
//fun BottomNavGraph(navController: NavHostController) {
//    NavHost(
//        navController = navController,
//        startDestination = BottomBarScreen.Home.route
//    ) {
//        composable(route = BottomBarScreen.Home.route) {
//            HomeScreen()
//        }
//        composable(route = BottomBarScreen.Home.route) {
//            //hiltViewModel(AddTaskScreen())
//            AddTaskScreen()
//        }
//        composable(route = BottomBarScreen.Calendar.route) {
//            CalendarScreen()
//        }
//    }
//}