package ru.sibfu.openkras.features.authentification.signIn

data class UserLoginState (
    val isLoading: Boolean = false,
    val error: String? = null,
    val name: String = "",
    val email: String = "",
    val password: String = ""
) {
    val isPasswordValid: Boolean =
            password.length >= 8 &&
            password.any { it.isUpperCase() } &&
            password.any { it.isLowerCase() }

    val isEmailValid: Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isLoginEnabled: Boolean = isEmailValid && isPasswordValid  && !isLoading
}