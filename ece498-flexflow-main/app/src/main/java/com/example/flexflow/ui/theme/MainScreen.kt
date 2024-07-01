//package com.example.flexflow.ui.theme
//
//import android.annotation.SuppressLint
//import androidx.compose.foundation.layout.RowScope
//import androidx.compose.material.BottomNavigation
//import androidx.compose.material.BottomNavigationItem
//import androidx.compose.material.ContentAlpha
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.graphics.Color
//import androidx.navigation.NavDestination
//import androidx.navigation.NavDestination.Companion.hierarchy
//import androidx.navigation.NavGraph.Companion.findStartDestination
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
//import dagger.hilt.internal.GeneratedComponent
//import dagger.internal.DaggerGenerated
//
//
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MainScreen() {
//    val navController = rememberNavController()
//    Scaffold(
//        bottomBar = { BottomBar(navController = navController) }
//    ) {
//        BottomNavGraph(navController = navController)
//    }
//}
//
//@Composable
//fun BottomBar(navController: NavHostController) {
//    val screens = listOf(
//        BottomBarScreen.Home,
//        BottomBarScreen.AddTask,
//        BottomBarScreen.Calendar,
//    )
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentDestination = navBackStackEntry?.destination
//
//    BottomNavigation (
//        backgroundColor = Color.LightGray
//            ) {
//        screens.forEach { screen ->
//            AddItem(
//                screen = screen,
//                currentDestination = currentDestination,
//                navController = navController
//            )
//        }
//    }
//}
//
//@Composable
//fun RowScope.AddItem(
//    screen: BottomBarScreen,
//    currentDestination: NavDestination?,
//    navController: NavHostController
//) {
//    BottomNavigationItem(
//        label = {
//            Text(text = screen.title)
//        },
//        icon = {
//            Icon(
//                imageVector = screen.icon,
//                contentDescription = "Navigation Icon"
//            )
//        },
//        selected = currentDestination?.hierarchy?.any {
//            it.route == screen.route
//        } == true,
//        unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
//        onClick = {
//            navController.navigate(screen.route) {
//                popUpTo(navController.graph.findStartDestination().id)
//                launchSingleTop = true
//            }
//        }
//    )
//}