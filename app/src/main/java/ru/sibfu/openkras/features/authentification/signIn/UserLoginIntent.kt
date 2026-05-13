package ru.sibfu.openkras.features.authentification.signIn

sealed class UserLoginIntent {
    data class EmailChange(val email: String) : UserLoginIntent()
    data class PasswordChange(val password: String) : UserLoginIntent()
    data object RegisterClicked : UserLoginIntent()
    data object LoginClicked : UserLoginIntent()
    data object ErrorDismissed : UserLoginIntent()
}
sealed class LoginEffect {
    data object NavigateToRegister: LoginEffect()
    data object NavigateToMain : LoginEffect()
    data class ShowSnackbar(val message: String) : LoginEffect()
}