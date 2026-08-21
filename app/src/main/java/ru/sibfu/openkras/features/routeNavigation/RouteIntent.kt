package ru.sibfu.openkras.features.routeNavigation

import com.yandex.mapkit.geometry.Point
import ru.sibfu.domain.PointModel


sealed interface RouteIntent {
    data object TogglePlayPause : RouteIntent
    data object ToggleAutoplay : RouteIntent
    data object NextPoint : RouteIntent
    data object PreviousPoint : RouteIntent
    data class CompletePoint(val pointId: Int) : RouteIntent
    data class SetPointsListVisible(val visible: Boolean) : RouteIntent
    data class SetAutoplaySheetVisible(val visible: Boolean) : RouteIntent
    data class CenterOnUser(val point: Point) : RouteIntent
    data object StartLocationTracking : RouteIntent

    data class SelectPoint(val point: PointModel) : RouteIntent

    data class SeekAudio(val progress: Float) : RouteIntent
    data object CompleteRoute : RouteIntent
}

sealed class RouteEffect{
    data object navigateToExcursion : RouteEffect()
}