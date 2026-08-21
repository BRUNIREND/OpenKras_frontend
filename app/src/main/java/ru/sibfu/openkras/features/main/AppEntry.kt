package ru.sibfu.openkras.features.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.sibfu.openkras.navigation.AppNavGraph
import ru.sibfu.openkras.navigation.MainScreenGraph
import ru.sibfu.openkras.navigation.MyBottomNavigation
import ru.sibfu.openkras.navigation.ProfileScreenGraph
import ru.sibfu.openkras.ui.theme.CustomTopAppBar

@Composable
fun AppEntry(
){

    val globalSnackbarHostState = remember { SnackbarHostState() }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination


    val userScreenDetails = listOf(
        ProfileScreenGraph.ProfileScreen :: class,
        ProfileScreenGraph.AboutApp :: class,
        ProfileScreenGraph.PrivacyPolicy :: class,
        ProfileScreenGraph.UsageCondition :: class,
    )
    val screenMain = listOf(
        MainScreenGraph.AllExcursionScreen::class,
        MainScreenGraph.FavoriteScreen::class
    )
    val isMainScreen = screenMain.any { currentDestination?.hasRoute(it) == true }
    val isUserScreenDetails = userScreenDetails.any { currentDestination?.hasRoute(it) == true }
    val shouldShowBottomBar = isMainScreen
    val shouldShowTopAppBar = isUserScreenDetails

    Scaffold (
        topBar = {
            if (shouldShowTopAppBar){

                val currentRouteClass = userScreenDetails.find {
                    currentDestination?.hasRoute(it) == true
                }

                val text = if (currentRouteClass != null) {

                    when {
                        currentDestination?.hasRoute<ProfileScreenGraph.AboutApp>() == true -> "О приложении"
                        currentDestination?.hasRoute<ProfileScreenGraph.UsageCondition>() == true -> "Условие использования"
                        currentDestination?.hasRoute<ProfileScreenGraph.PrivacyPolicy>() == true -> "Политика конфиденциальности"
                        currentDestination?.hasRoute<ProfileScreenGraph.ProfileScreen>() == true -> "Профиль"
                        else -> "Профиль"
                    }
                } else {
                    "Меню"
                }
                CustomTopAppBar(
                    nameScreen = text,
                    onBackClick = {navController.popBackStack()}
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = globalSnackbarHostState) },
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
            navController = navController,
            snackbarHostState = globalSnackbarHostState
        )
    }
}