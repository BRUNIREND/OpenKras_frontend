package ru.sibfu.openkras.features.routeNavigation

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.sibfu.domain.usecase.exception.NetworkResult
import ru.sibfu.domain.usecase.excursionUseCase.CompleteExcursionUseCase
import ru.sibfu.domain.usecase.excursionUseCase.GetExcursionById
import ru.sibfu.openkras.PlaybackService
import javax.inject.Inject

@HiltViewModel
class RouteNavigationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationClient: FusedLocationProviderClient,
    private val getExcursionById: GetExcursionById,
    private val completeExcursionUseCase: CompleteExcursionUseCase,
) : ViewModel() {

    private var locationCallback: LocationCallback? = null

    // 1. Вместо ExoPlayer объявляем MediaController
    private var mediaController: MediaController? = null

    private val _state = MutableStateFlow(RouteNavigationState())
    val state: StateFlow<RouteNavigationState> = _state.asStateFlow()

    private val _effect = Channel<RouteEffect>()
    val effect = _effect.receiveAsFlow()
    private var progressJob: Job? = null
    private var excursionId: Int? = null

    // 2. Создаем постоянный листенер для плеера
    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            mediaController?.let { player ->
                if (playbackState == Player.STATE_READY) {
                    _state.update { it.copy(durationMs = player.duration.coerceAtLeast(0L)) }
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _state.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) startProgressTicker() else stopProgressTicker()
        }
    }

    init {
        // 3. Асинхронно подключаемся к нашему фоновому сервису
        initMediaController()
    }

    private fun initMediaController() {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            try {
                mediaController = controllerFuture.get().apply {
                    addListener(playerListener)
                }
                // На случай, если плеер уже что-то играл в фоне, синхронизируем состояние UI
                mediaController?.let { player ->
                    _state.update { it.copy(isPlaying = player.isPlaying) }
                    if (player.isPlaying) startProgressTicker()
                }
            } catch (e: Exception) {
                Log.e("RouteViewModel", "Не удалось запустить MediaController", e)
            }
        }, context.mainExecutor)
    }

    fun loadRouteData(id: Int){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            excursionId = id
            when (val result = getExcursionById(id)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(
                        excursionTitle = result.data.title,
                        points = result.data.points
                    ) }
                }
                is NetworkResult.Error -> { _state.update { it.copy(isLoading = false, error = result.message) } }
                is NetworkResult.Exception -> { _state.update { it.copy(isLoading = false, error = result.e.message) } }
            }
        }
    }

    fun handleIntent(intent: RouteIntent) {
        when (intent) {
            is RouteIntent.TogglePlayPause -> togglePlayback()
            is RouteIntent.SeekAudio -> seekTo(intent.progress)
            is RouteIntent.ToggleAutoplay -> _state.update { it.copy(isAutoplayEnabled = !it.isAutoplayEnabled) }
            is RouteIntent.NextPoint -> changePoint(1)
            is RouteIntent.CompletePoint -> {
                _state.update { old -> old.copy(completedPointIds = old.completedPointIds + intent.pointId) }
            }
            is RouteIntent.SetPointsListVisible -> _state.update { it.copy(showPointsListScreen = intent.visible) }
            is RouteIntent.SetAutoplaySheetVisible -> _state.update { it.copy(showAutoplayBottomSheet = intent.visible) }
            is RouteIntent.CenterOnUser -> _state.update { it.copy(targetForCamer = intent.point) }
            is RouteIntent.PreviousPoint -> changePoint(-1)
            RouteIntent.StartLocationTracking -> startLocationTracking()
            is RouteIntent.SelectPoint -> {
                val index = _state.value.points.indexOfFirst { it.id == intent.point.id }
                if (index != -1 && index != _state.value.currentPointIndex) {
                    if (index == _state.value.points.lastIndex){
                        _state.update { it.copy(isLastPoint = true) }
                    } else {
                        _state.update { it.copy(currentPointIndex = index, isLastPoint = false) }
                    }
                    prepareNextAudio()
                }
            }
            is RouteIntent.CompleteRoute -> {
                viewModelScope.launch { completeExcursionUseCase(excursionId!!) }
                _effect.trySend(RouteEffect.navigateToExcursion)
            }
        }
    }

    private fun changePoint(delta: Int) {
        _state.update { old ->
            val newIndex = (old.currentPointIndex + delta).coerceIn(0, old.points.lastIndex)
            if (newIndex != old.currentPointIndex) {
                if (newIndex == _state.value.points.lastIndex){
                    _state.update { it.copy(isLastPoint = true) }
                } else {
                    _state.update { it.copy(isLastPoint = false)}
                }
                prepareNextAudio()
                old.copy(currentPointIndex = newIndex)
            } else old
        }
    }

    private fun togglePlayback() {
        // Ждем, пока контроллер подключится к сервису
        val player = mediaController ?: return
        val currentPoint = _state.value.currentPoint ?: return
        val audioUrl = currentPoint.audioUrl.firstOrNull() ?: return
        val correctedUrl = audioUrl.replace("localhost:9000", "10.0.2.2:9000")
            .replace("127.0.0.1:9000", "10.0.2.2:9000")

        if (player.currentMediaItem == null) {
            // 4. ДОБАВЛЯЕМ МЕТАДАННЫЕ ДЛЯ СИСТЕМНОЙ ШТОРКИ
            val metadata = MediaMetadata.Builder()
                .setTitle(currentPoint.name) // Название аудиогида/точки
                .setArtist(_state.value.excursionTitle) // Название всей экскурсии
                .build()

            val mediaItem = MediaItem.Builder()
                .setUri(correctedUrl)
                .setMediaMetadata(metadata)
                .build()

            player.setMediaItem(mediaItem)
            player.prepare()
        }

        if (player.isPlaying) player.pause() else player.play()
    }

    private fun seekTo(progress: Float) {
        val player = mediaController ?: return
        val duration = _state.value.durationMs
        if (duration > 0) {
            val seekPosition = (progress * duration).toLong()
            player.seekTo(seekPosition)
            _state.update { it.copy(currentPositionMs = seekPosition) }
        }
    }

    private fun startProgressTicker() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                mediaController?.let { player ->
                    if (player.isPlaying) {
                        _state.update { it.copy(currentPositionMs = player.currentPosition) }
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopProgressTicker() {
        progressJob?.cancel()
    }

    private fun prepareNextAudio() {
        mediaController?.stop()
        mediaController?.clearMediaItems()
        _state.update { it.copy(isPlaying = false, currentPositionMs = 0L, durationMs = 0L) }
        stopProgressTicker()
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationTracking()


        mediaController?.removeListener(playerListener)
        mediaController?.release()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationTracking() {
        if (locationCallback != null) return
        Log.d("RouteViewModel", "Запуск отслеживания геопозиции")

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).apply {
            setMinUpdateIntervalMillis(2000L)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation = locationResult.lastLocation ?: return
                _state.update { currentState ->
                    currentState.copy(userLatitude = lastLocation.latitude, userLongitude = lastLocation.longitude)
                }
                Log.d("RouteViewModel", "Новые координаты: ${lastLocation.latitude}, ${lastLocation.longitude}")
            }
        }
        locationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
    }

    private fun stopLocationTracking() {
        locationCallback?.let { callback ->
            locationClient.removeLocationUpdates(callback)
            locationCallback = null
            Log.d("RouteViewModel", "Отслеживание геопозиции остановлено")
        }
    }
}