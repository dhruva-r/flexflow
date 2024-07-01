//package com.example.flexflow.ui.theme
//
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.DateRange
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Settings
//import androidx.compose.ui.graphics.vector.ImageVector
//
////This will hold all the screens for the bottom Navbar
//sealed class BottomBarScreen(
//    val route: String,
//    val title: String,
//    val icon: ImageVector
//) {
//
//    //Each object represents a screen in our bot nav bar
//    object Home : BottomBarScreen(
//        route = "home",
//        title = "Home",
//        icon = Icons.Default.Home
//    )
//
//    object Sched : BottomBarScreen(
//        route = "sched",
//        title = "Journal",
//        icon = Icons.Default.Edit
//    )
//
//    object Calendar : BottomBarScreen(
//        route = "calendar",
//        title = "Calendar",
//        icon = Icons.Default.DateRange
//    )
//
//    object AddTask : BottomBarScreen(
//        route = "add task",
//        title = "Task",
//        icon = Icons.Default.DateRange
//    )
//}