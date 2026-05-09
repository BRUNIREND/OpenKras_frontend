package ru.sibfu.openkras.navigation

import kotlinx.serialization.Serializable

sealed class MainScreenGraph{
    @Serializable
    data object AllExcursionScreen : MainScreenGraph()

    @Serializable
    data object FavoriteScreen : MainScreenGraph()

    @Serializable
    data object ProfileScreen : MainScreenGraph()

    @Serializable
    data class ExcursionDetails(val id: Int) : MainScreenGraph()
}


sealed class AuthScreenGraph{

    @Serializable
    data object LoginScreen : AuthScreenGraph()

    @Serializable
    data object RegistrationScreen : AuthScreenGraph()

}