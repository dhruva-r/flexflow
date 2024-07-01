package com.example.flexflow

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector) {
    object Home : Screen("Home", Icons.Filled.Home)
    object AddTasks : Screen("Add Task", Icons.Filled.List)
    object Tasks : Screen("Tasks", Icons.Filled.List)

    object AddJournal : Screen("AddJournal", Icons.Filled.Book)
    object Journals : Screen("Journals", Icons.Filled.Book)
    object Profile : Screen("Profile", Icons.Filled.Person)
    object Settings : Screen("Settings", Icons.Filled.Settings)
}

