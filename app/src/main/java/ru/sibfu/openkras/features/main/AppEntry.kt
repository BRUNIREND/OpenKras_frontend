package ru.sibfu.openkras.features.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.sibfu.openkras.navigation.AppNavGraph
import ru.sibfu.openkras.navigation.AuthScreenGraph
import ru.sibfu.openkras.navigation.BottomNavItem
import ru.sibfu.openkras.navigation.MyBottomNavigation

@Composable
fun AppEntry(
    mainViewModel: MainViewModel = hiltViewModel()
){
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val authScreens = listOf(
        AuthScreenGraph.RegistrationScreen::class,
        AuthScreenGraph.LoginScreen::class,
        AuthScreenGraph.OtpScreen::class,
    )
    val isAuthScreen = authScreens.any { currentDestination?.hasRoute(it) == true }
    val shouldShowBottomBar = !isAuthScreen

    Scaffold (
        bottomBar = {
            if (shouldShowBottomBar){
                MyBottomNavigation(
                    navController = navController
                )
            }
        }
    ){ innerPadding ->
        AppNavGraph(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}