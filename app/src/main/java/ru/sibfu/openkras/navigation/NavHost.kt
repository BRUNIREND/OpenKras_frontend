package ru.sibfu.openkras.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.sibfu.openkras.features.excursion.ExcursionViewModel
import ru.sibfu.openkras.features.excursion.ListExcursionScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
){
    NavHost(
        navController = navController,
        startDestination = MainScreenGraph.AllExcursionScreen,
        modifier = modifier
    ){
        composable<MainScreenGraph.AllExcursionScreen>{
            val viewModel = hiltViewModel<ExcursionViewModel>()
            ListExcursionScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}