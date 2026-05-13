package ru.sibfu.openkras.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import ru.sibfu.openkras.features.authentification.signIn.UserLoginScreen
import ru.sibfu.openkras.features.authentification.signUp.UserRegistrationScreen
import ru.sibfu.openkras.features.authentification.signUp.otp.OtpScreen
import ru.sibfu.openkras.features.excursion.ExcursionViewModel
import ru.sibfu.openkras.features.excursion.ListExcursionScreen
import ru.sibfu.openkras.features.main.MainState
import ru.sibfu.openkras.features.main.MainViewModel

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController,
){
    val startState by mainViewModel.startDestination.collectAsState()


    if (startState is MainState.Authorized) {
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
    else {
        NavHost(
            navController = navController,
            startDestination = AuthScreenGraph.LoginScreen,
            modifier = modifier
        ){composable<AuthScreenGraph.RegistrationScreen>{
            UserRegistrationScreen(

                onNavigateToOtp = { email ->
                    navController.navigate(AuthScreenGraph.OtpScreen(email))
                },
                onNavigateToLogin = {
                    navController.navigate(AuthScreenGraph.LoginScreen){
                        popUpTo(AuthScreenGraph.RegistrationScreen) { inclusive = true }
                    }
                }
            )
        }

            composable<AuthScreenGraph.OtpScreen> { backStackEntry ->
                val args = backStackEntry.toRoute<AuthScreenGraph.OtpScreen>()
                OtpScreen(email = args.email)
            }

            composable<AuthScreenGraph.LoginScreen> {
                UserLoginScreen(
                    onNavigateToMain = {
                        navController.navigate(MainScreenGraph.AllExcursionScreen)
                    },
                    onNavigateToRegistration = {
                        navController.navigate(AuthScreenGraph.RegistrationScreen){
                            popUpTo(AuthScreenGraph.LoginScreen){ inclusive = true}
                        }
                    }
                )
            }
        }
    }
}