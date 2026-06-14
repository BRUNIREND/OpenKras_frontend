package ru.sibfu.openkras.features.user

data class UserScreenState (
    val isLoading: Boolean = false,
    val error: String? = null,
    val name: String = "",
    val email: String = "",
)