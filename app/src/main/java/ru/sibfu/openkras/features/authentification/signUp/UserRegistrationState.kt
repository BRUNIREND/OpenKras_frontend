package ru.sibfu.openkras.features.authentification.signUp

data class UserRegistrationState (
    val isLoading: Boolean = false,
    val error: String? = null,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
) {
    val isEmailValid: Boolean
        get() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordValid: Boolean = password.length >= 8 &&
            password.any { it.isUpperCase() } &&
            password.any { it.isLowerCase() }

    val passwordsMatch: Boolean = password == confirmPassword && password.isNotEmpty()

    val isRegisterEnabled: Boolean = name.isNotBlank() &&
            email.contains("@") &&
            isPasswordValid &&
            passwordsMatch &&
            isEmailValid &&
            !isLoading
}