package ru.sibfu.openkras.features.authentification.signUp.otp


data class OtpState(
    val email: String = "",
    val code: String = "", // Код из письма
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class OtpIntent {
    data class CodeChanged(val value: String) : OtpIntent()
    object VerifyClicked : OtpIntent()
}

sealed class OtpEffect {
    object NavigateToMain : OtpEffect()
}