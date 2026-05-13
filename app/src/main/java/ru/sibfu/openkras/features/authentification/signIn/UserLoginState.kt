package ru.sibfu.openkras.features.authentification.signIn

data class UserLoginState (
    val isLoading: Boolean = false,
    val error: String? = null,
    val username: String = "",
    val email: String = "",
    val password: String = ""
)