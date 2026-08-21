package ru.sibfu.openkras.features.routeNavigation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yandex.mapkit.geometry.Point
import ru.sibfu.openkras.ui.theme.YandexMapView


@Composable
fun RouteNavigationScreen(
    onExitRoute: () -> Unit,
    excursionId: Int,
    viewModel: RouteNavigationViewModel = hiltViewModel(),
    onNavigateToExcursionList: () -> Unit,
) {
    val state by viewModel.state.collectAsState()


    LaunchedEffect(excursionId) {
        viewModel.loadRouteData(excursionId)
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RouteEffect.navigateToExcursion -> {
                    onNavigateToExcursionList()
                }
            }
        }
    }
    RouteNavigationContent(
        state = state,
        onIntent = { viewModel.handleIntent(it) },
        onExitRoute = onExitRoute
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteNavigationContent(
    state: RouteNavigationState = RouteNavigationState(),
    onIntent: (RouteIntent) -> Unit,
    onExitRoute: () -> Unit
) {
    var activePhotoIndex by remember { mutableStateOf<Int?>(null) }
    val burgundyColor = Color(0xFF8B1A34)
    val lightGrayBackground = Color(0xFFF5F5F5)

    val peekHeight = 250.dp

    // Лаунчер для запроса системных разрешений на локацию
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted || coarseLocationGranted) {
            onIntent(RouteIntent.StartLocationTracking)
        } else {
            // Пользователь отказал в доступе. Тут можно показать Snackbar или обработать иначе
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }




    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = false
        )
    )


    BoxWithConstraints(modifier = Modifier.fillMaxSize()){
        val fullHeight = constraints.maxHeight.toFloat()
        val peekHeightPx = with(LocalDensity.current) { peekHeight.toPx() }

        val sheetProgress by remember {
            derivedStateOf {
                val currentOffset = runCatching {
                    scaffoldState.bottomSheetState.requireOffset()
                }.getOrNull() ?: fullHeight

                // partialOffset — когда виден только заголовок (внизу)
                val partialOffset = fullHeight - peekHeightPx
                // expandedOffset — когда шторка развернута (вверху, оставляем отступ 10%)
                val expandedOffset = fullHeight * 0.1f

                // Вычисляем прогресс от 0f (свернуто) до 1f (развернуто)
                ((partialOffset - currentOffset) / (partialOffset - expandedOffset)).coerceIn(0f, 1f)
            }
        }


        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 250.dp,
            sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            sheetContainerColor = Color.White,
            sheetContent = {
                state.currentPoint?.let { currentPoint ->
                    CurrentPointDetailsSheet(
                        point = currentPoint.apply { position = state.currentPointIndex + 1},
                        isFirstPoint = state.currentPointIndex == 0,
                        isPlaying = state.isPlaying,
                        currentPositionMs = state.currentPositionMs,
                        burgundyColor = burgundyColor,
                        lightGrayBackground = lightGrayBackground,
                        onIntent = onIntent,
                        onImageClick = { index -> activePhotoIndex = index },
                        durationMs = state.durationMs,
                        isLastPoint = state.isLastPoint,
                        sheetProgress = sheetProgress
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 1. Слой интерактивной карты
                val hasLocation = state.userLatitude != null && state.userLongitude != null

                if (hasLocation) {
                    YandexMapView(
                        points = state.points,
                        currentPointIndex = state.currentPointIndex,
                        userLatitude = state.userLatitude,
                        userLongitude = state.userLongitude,
                        modifier = Modifier.fillMaxSize(),
                        onPointClick = { point ->
                            onIntent(RouteIntent.SelectPoint(point))

                        },
                        cameraTarget = state.targetForCamer
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(color = burgundyColor)
                            Text(
                                text = "Определение вашего местоположения...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        tonalElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(onClick = { onIntent(RouteIntent.SetPointsListVisible(true)) }) {
                                Icon(Icons.Default.Menu, contentDescription = "Список точек", tint = Color.Black)
                            }
                            Text(
                                text = state.excursionTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1.0F)
                            )
                            IconButton(onClick = onExitRoute) {
                                Icon(Icons.Default.Close, contentDescription = "Завершить", tint = Color.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Линия прогресса
                    val progress = if (state.totalPointsCount > 0) state.completedPointsCount.toFloat() / state.totalPointsCount else 0f
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(CircleShape),
                            color = Color(0xFF27AE60),
                            trackColor = Color.LightGray.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "${state.completedPointsCount}/${state.totalPointsCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }

                // 3. Кнопка центрирования геолокации (FAB)
                FloatingActionButton(
                    onClick = { onIntent(RouteIntent.CenterOnUser(
                        Point(
                            state.userLatitude!!,
                            state.userLongitude!!
                        )))
                    },
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 32.dp, end = 16.dp)
                        .size(48.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Моя геопозиция")
                }
            }
        }
    }

    // 4. Полноэкранный просмотр картинок
    if (activePhotoIndex != null && state.currentPoint != null) {
        val validImages = state.currentPoint!!.images.filterNotNull()

        if (validImages.isNotEmpty()) {
            FullscreenPhotoViewer(
                images = validImages,
                initialIndex = activePhotoIndex!!,
                onClose = { activePhotoIndex = null }
            )
        }
    }

    // Дополнительное окно автовоспроизведения
    if (state.showAutoplayBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { onIntent(RouteIntent.SetAutoplaySheetVisible(false)) },
            containerColor = Color.White
        ) {
            AutoplaySettingsContent(
                isEnabled = state.isAutoplayEnabled,
                onToggle = { onIntent(RouteIntent.ToggleAutoplay) },
                burgundyColor = burgundyColor
            )
        }
    }

    // Экран-оверлей со списком всех точек (Таймлайн)
    AnimatedVisibility(
        visible = state.showPointsListScreen,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        RoutePointsOverlay(
            points = state.points,
            completedPointIds = state.completedPointIds,
            currentPointIndex = state.currentPointIndex,
            burgundyColor = burgundyColor,
            onClose = { onIntent(RouteIntent.SetPointsListVisible(false)) }
        )
    }
}