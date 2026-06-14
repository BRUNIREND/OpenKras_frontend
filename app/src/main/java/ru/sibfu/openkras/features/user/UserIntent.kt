package ru.sibfu.openkras.features.user

sealed class UserScreenIntent {

    data object OnLogoutClick : UserScreenIntent()
    data object UsageConditionClicked : UserScreenIntent()
    data object PrivacyPolicyClicked : UserScreenIntent()
    data object AboutAppClicked : UserScreenIntent()

}
sealed class ScreenEffect {
    data object NavigateToUsageCondition : ScreenEffect()
    data object NavigateToPrivacyPolicy : ScreenEffect()
    data object NavigateToAboutApp : ScreenEffect()
    data object NavigateToLogin : ScreenEffect()
    data class ShowSnackbar(val message: String) : ScreenEffect()
}