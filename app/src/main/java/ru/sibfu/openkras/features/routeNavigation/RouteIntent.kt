package ru.sibfu.openkras.features.routeNavigation

sealed interface RouteIntent {
    data object TogglePlayPause : RouteIntent
    data object ToggleAutoplay : RouteIntent
    data object NextPoint : RouteIntent
    data class CompletePoint(val pointId: Int) : RouteIntent
    data class SetPointsListVisible(val visible: Boolean) : RouteIntent
    data class SetAutoplaySheetVisible(val visible: Boolean) : RouteIntent
    data object CenterOnUser : RouteIntent
}