package ru.sibfu.openkras.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@Composable
fun AppEntry(){
    val navController = rememberNavController()

    Scaffold (
        bottomBar = { MyBottomNavigation(
            navController = navController
        ) }
    ){ innerPadding ->
        AppNavGraph(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}