package ru.sibfu.openkras.features.splashScreen

sealed interface SplashNavigationState {
    data object Loading : SplashNavigationState
    data object NavigateToLogin : SplashNavigationState
    data object NavigateToMain : SplashNavigationState
    data class ShowSnackbar(val message: String, val code: Int?) : SplashNavigationState

}

data class SplashState(
    val navigationState: SplashNavigationState = SplashNavigationState.Loading
)