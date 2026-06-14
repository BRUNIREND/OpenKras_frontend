package ru.sibfu.openkras.features.routeNavigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.sibfu.domain.PointModel
import javax.inject.Inject

@HiltViewModel
class RouteNavigationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(RouteNavigationState())
    val state: StateFlow<RouteNavigationState> = _state.asStateFlow()

    init {
        loadRouteData()
    }

    private fun loadRouteData() {
        _state.update {
            it.copy(
                excursionTitle = "Путешествие по Красноярску с девочкой-фантомом",
                points = listOf(
                    PointModel(
                        id = 101, position = 1, name = "Пушкинский городской театр",
                        description = "История театра...", address = "пр. Мира, 37",
                        latitude = 56.012, longitude = 92.871, radiusMeters = 20,
                        audioUrl = listOf("https://example.com/audio1.mp3"), images = emptyList()
                    ),
                    PointModel(
                        id = 102, position = 2, name = "Губернская мужская гимназия",
                        description = "О гимназии...", address = "пр. Мира, 37",
                        latitude = 56.014, longitude = 92.875, radiusMeters = 25,
                        audioUrl = listOf("https://example.com/audio2.mp3"), images = emptyList()
                    ),
                    PointModel(
                        id = 103, position = 4, name = "Учительская семинария",
                        description = "О семинарии...", address = "пр. Мира, 37",
                        latitude = 56.015, longitude = 92.879, radiusMeters = 15,
                        audioUrl = listOf(null), images = emptyList()
                    )
                )
            )
        }
    }

    fun handleIntent(intent: RouteIntent) {
        when (intent) {
            RouteIntent.TogglePlayPause -> _state.update { it.copy(isPlaying = !it.isPlaying) }
            RouteIntent.ToggleAutoplay -> _state.update { it.copy(isAutoplayEnabled = !it.isAutoplayEnabled) }
            RouteIntent.NextPoint -> {
                _state.update { old ->
                    val nextIndex = (old.currentPointIndex + 1).coerceAtMost(old.points.lastIndex)
                    old.copy(currentPointIndex = nextIndex)
                }
            }
            is RouteIntent.CompletePoint -> {
                _state.update { old ->
                    old.copy(completedPointIds = old.completedPointIds + intent.pointId)
                }
            }
            is RouteIntent.SetPointsListVisible -> _state.update { it.copy(showPointsListScreen = intent.visible) }
            is RouteIntent.SetAutoplaySheetVisible -> _state.update { it.copy(showAutoplayBottomSheet = intent.visible) }
            RouteIntent.CenterOnUser -> { /* Логика работы с картой */ }
        }
    }
}