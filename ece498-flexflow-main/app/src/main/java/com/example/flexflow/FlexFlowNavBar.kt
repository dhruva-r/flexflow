package com.example.flexflow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlexFlowNavBar(
    navController: NavHostController
) {

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    val items = listOf(
        Screen.Home,
        Screen.Tasks,
        Screen.Journals,
//        Screen.Profile,
        Screen.Settings
    )

    NavigationBar(modifier = Modifier) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute =
            backStackEntry?.destination?.route ?: Screen.Home.route
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            NavigationBarItem(
                selected = currentRoute == items[0].route,
                icon = { Icon(items[0].icon, contentDescription = null) },
                label = { Text(text = items[0].route)},
                onClick = {
                    if (currentRoute != items[0].route) {
                        navController.navigate(items[0].route)
                    }
                }
            )
            NavigationBarItem(
                selected = currentRoute == items[1].route,
                icon = { Icon(items[1].icon, contentDescription = null) },
                label = { Text(text = items[1].route)},
                onClick = {
                    if (currentRoute != items[1].route) {
                        navController.navigate(items[1].route)
                    }
                }
            )
            NavigationBarItem(
                selected = currentRoute == items[2].route,
                icon = { Icon(items[2].icon, contentDescription = null) },
                label = { Text(text = items[2].route)},
                onClick = {
                    if (currentRoute != items[2].route) {
                        navController.navigate(items[2].route)
                    }
                }
            )
            NavigationBarItem(
                selected = currentRoute == items[3].route,
                icon = { Icon(items[3].icon, contentDescription = null) },
                label = { Text(text = items[3].route)},
                onClick = {
                    if (currentRoute != items[3].route) {
                        navController.navigate(items[3].route)
                    }
                }
            )
//            NavigationBarItem(
//                selected = currentRoute == items[4].route,
//                icon = { Icon(items[4].icon, contentDescription = null) },
//                label = { Text(text = items[4].route)},
//                onClick = {
//                    if (currentRoute != items[4].route) {
//                        navController.navigate(items[4].route)
//                    }
//                }
//            )
        }
    }

}
