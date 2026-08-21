package ru.sibfu.openkras.features.routeNavigation

import com.yandex.mapkit.geometry.Point
import ru.sibfu.domain.PointModel

data class RouteNavigationState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val excursionTitle: String = "",
    val points: List<PointModel> = emptyList(),
    val currentPointIndex: Int = 0,

    // Храним ID только пройденных точек
    val completedPointIds: Set<Int> = emptySet(),

    val userLatitude: Double? = null,
    val userLongitude: Double? = null,

    // Состояние аудиогида
    val isPlaying: Boolean = false,
    val audioProgress: Float = 0.0f,
    val isAutoplayEnabled: Boolean = true,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,

    // Видимость экранов/шторок
    val showPointsListScreen: Boolean = false,
    val showAutoplayBottomSheet: Boolean = false,

    val targetForCamer: Point? = null,
    val isLastPoint: Boolean = false
) {
    val currentPoint: PointModel? get() = points.getOrNull(currentPointIndex)
    val completedPointsCount: Int get() = points.count { completedPointIds.contains(it.id) }
    val totalPointsCount: Int get() = points.size
}
