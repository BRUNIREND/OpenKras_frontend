package ru.sibfu.openkras.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import ru.sibfu.openkras.features.authentification.signIn.UserLoginScreen
import ru.sibfu.openkras.features.authentification.signUp.UserRegistrationScreen
import ru.sibfu.openkras.features.authentification.signUp.otp.OtpScreen
import ru.sibfu.openkras.features.excursion.ExcursionViewModel
import ru.sibfu.openkras.features.excursion.ListExcursionScreen
import ru.sibfu.openkras.features.excursion.excursionDetail.ExcursionDetailScreen
import ru.sibfu.openkras.features.excursion.excursionDetail.ExcursionDetailViewModel
import ru.sibfu.openkras.features.favorites.FavoritesScreen
import ru.sibfu.openkras.features.favorites.FavoritesViewModel
import ru.sibfu.openkras.features.main.MainViewModel
import ru.sibfu.openkras.features.routeNavigation.RouteNavigationScreen
import ru.sibfu.openkras.features.routeNavigation.RouteNavigationViewModel
import ru.sibfu.openkras.features.splashScreen.SplashScreen
import ru.sibfu.openkras.features.user.UserScreen
import ru.sibfu.openkras.features.user.sideScreens.AboutAppScreen
import ru.sibfu.openkras.features.user.sideScreens.PrivacyPolicyScreen
import ru.sibfu.openkras.features.user.sideScreens.UsageConditionsScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
){
    NavHost(
        navController = navController,
        startDestination = AuthScreenGraph.SplashScreen,
        modifier = modifier
    ){
        composable<AuthScreenGraph.SplashScreen>{
            SplashScreen(
                snackbarHostState = snackbarHostState,
                onNavigateToLogin = {
                    navController.navigate(AuthScreenGraph.LoginScreen){
                        popUpTo(AuthScreenGraph.RegistrationScreen) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(MainScreenGraph.AllExcursionScreen){
                        popUpTo(AuthScreenGraph.RegistrationScreen) { inclusive = true }
                    }
                },
            )
        }
        composable<MainScreenGraph.AllExcursionScreen>{
            val viewModel = hiltViewModel<ExcursionViewModel>()
            ListExcursionScreen(
                snackbarHostState = snackbarHostState,
                viewModel = viewModel,
                onNavigateToDetail = { excursionId ->
                    navController.navigate(ExcursionScreenGraph.DetailScreen(excursionId))
                }
            )
        }
        composable<ExcursionScreenGraph.DetailScreen>{
            val args = it.toRoute<ExcursionScreenGraph.DetailScreen>()
            val viewModel = hiltViewModel<ExcursionDetailViewModel>()
            ExcursionDetailScreen(
                excursionId = args.excursionId,
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel,
                onExcursionStart = { excursionId ->
                    navController.navigate(ExcursionScreenGraph.StartExcursion(excursionId))
                }
            )
        }
        composable<ExcursionScreenGraph.StartExcursion> {
            val args = it.toRoute<ExcursionScreenGraph.StartExcursion>()
            val viewModel = hiltViewModel<RouteNavigationViewModel>()
            RouteNavigationScreen(
                excursionId = args.excursionId,
                viewModel = viewModel,
                onExitRoute = { navController.popBackStack() },
                onNavigateToExcursionList = {navController.navigate(MainScreenGraph.AllExcursionScreen)}
            )
        }
        composable<ProfileScreenGraph.AboutApp>{
            AboutAppScreen()
        }
        composable<ProfileScreenGraph.PrivacyPolicy>{
            PrivacyPolicyScreen()
        }
        composable<ProfileScreenGraph.UsageCondition>{
            UsageConditionsScreen()
        }
        composable<MainScreenGraph.FavoriteScreen>{
            val viewModel = hiltViewModel<FavoritesViewModel>()
            FavoritesScreen(
                snackbarHostState = snackbarHostState,
                viewModel = viewModel,
                onNavigateToExcursion = { excursionId ->
                    navController.navigate(ExcursionScreenGraph.DetailScreen(excursionId))
                }
            )
        }

        composable<ProfileScreenGraph.ProfileScreen>{
            UserScreen(
                onNavigateToAboutApp = {navController.navigate(ProfileScreenGraph.AboutApp)},
                onNavigateToPrivacyPolicy = {navController.navigate(ProfileScreenGraph.PrivacyPolicy)},
                onNavigateToUsageCondition = {navController.navigate(ProfileScreenGraph.UsageCondition)},
                onNavigateToLogin = {
                    navController.navigate(AuthScreenGraph.LoginScreen){
                        popUpTo(ProfileScreenGraph.ProfileScreen){
                            inclusive = true
                        }
                    }
                },
                snackbarHostState = snackbarHostState
            )
        }

        composable<AuthScreenGraph.RegistrationScreen>{
            UserRegistrationScreen(
                snackbarHostState = snackbarHostState,
                onNavigateToOtp = { email ->
                    navController.navigate(AuthScreenGraph.OtpScreen(email))
                },
                onNavigateToLogin = {
                    navController.navigate(AuthScreenGraph.LoginScreen){
                        popUpTo(AuthScreenGraph.RegistrationScreen) { inclusive = true }
                    }
                },
            )
        }

        composable<AuthScreenGraph.OtpScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<AuthScreenGraph.OtpScreen>()
            OtpScreen(
                snackbarHostState = snackbarHostState,
                email = args.email,
                onNavigateToMain = {
                    navController.navigate(MainScreenGraph.AllExcursionScreen)
                },
            )
        }

        composable<AuthScreenGraph.LoginScreen> {
            UserLoginScreen(
                snackbarHostState = snackbarHostState,
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