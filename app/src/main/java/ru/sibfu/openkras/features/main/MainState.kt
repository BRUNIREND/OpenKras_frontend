package ru.sibfu.openkras.features.main

sealed class MainState {
    data object Loading : MainState()
    data object Authorized : MainState()
    data object Unauthorized : MainState()
}

