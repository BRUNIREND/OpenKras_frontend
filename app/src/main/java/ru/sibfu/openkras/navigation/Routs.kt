package ru.sibfu.openkras.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed class MainScreenGraph{
    @Serializable
    data object AllExcursionScreen : MainScreenGraph()
    @Serializable
    data object FavoriteScreen : MainScreenGraph()

}

@Serializable
sealed class ExcursionScreenGraph{
    @Serializable
    data class DetailScreen(val excursionId: Int) : ExcursionScreenGraph()

    @Serializable
    data class StartExcursion(val excursionId: Int) : ExcursionScreenGraph()

}

@Serializable
sealed class ProfileScreenGraph{
    @Serializable
    data object ProfileScreen : ProfileScreenGraph()

    @Serializable
    data object UsageCondition : ProfileScreenGraph()

    @Serializable
    data object PrivacyPolicy : ProfileScreenGraph()

    @Serializable
    data object AboutApp : ProfileScreenGraph()
}

@Serializable
sealed class AuthScreenGraph{
    @Serializable
    data object SplashScreen : AuthScreenGraph()
    @Serializable
    data object LoginScreen : AuthScreenGraph()
    @Serializable
    data object RegistrationScreen : AuthScreenGraph()
    @Serializable
    data class OtpScreen(val email: String) : AuthScreenGraph()
}