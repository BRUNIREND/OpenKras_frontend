package ru.sibfu.openkras.features.authentification.signUp

sealed class UserRegistrationIntent {
    data class NameChange(val name: String) : UserRegistrationIntent()
    data class EmailChange(val email: String) : UserRegistrationIntent()
    data class PasswordChange(val password: String) : UserRegistrationIntent()

    data class ConfirmPasswordChanged(val confirmPassword: String) : UserRegistrationIntent()
    data object Register : UserRegistrationIntent()
    data object LoginClicked: UserRegistrationIntent()
    data object ErrorDismissed : UserRegistrationIntent()

}

sealed class UserRegistrationEffect {
    data object NavigateToLogin : UserRegistrationEffect()
    data class NavigateToOTP(val email: String) : UserRegistrationEffect()
    data class ShowSnackbar(val message: String) : UserRegistrationEffect()
}