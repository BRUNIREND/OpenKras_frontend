package ru.sibfu.openkras.features.authentification.signUp.otp


data class OtpState(
    val email: String? = null,
    val code: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val codeLength: Int = 6,
    val secondsLeft: Int = 0,
){
    val isResendEnabled: Boolean get() = secondsLeft <= 0 && !isLoading
}

sealed class OtpIntent {
    data class CodeChanged(val value: String) : OtpIntent()
    data object VerifyClicked : OtpIntent()
    data object ResendClicked : OtpIntent()
    data object navigateToAllScreen : OtpIntent()
}

sealed class OtpEffect {
    data class ShowSnackbar(val message: String) : OtpEffect()
    data object NavigateToMain : OtpEffect()
}