package ru.sibfu.openkras.features.routeNavigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.sibfu.openkras.ui.theme.YandexMapView



@Composable
fun RouteNavigationScreen(
    onExitRoute: () -> Unit,
    excursionId: Int,
    viewModel: RouteNavigationViewModel = hiltViewModel()
){
    val state by viewModel.state.collectAsState()
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
    onExitRoute: () -> Unit,
) {
    val burgundyColor = Color(0xFF8B1A34)

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. Карта (Заглушка)
        val userLoc = Pair(state.userLatitude, state.userLongitude)

        YandexMapView(
            points = state.points,
            currentPointIndex = state.currentPointIndex,
            userLocation = userLoc as Pair<Double, Double>?,
            modifier = Modifier.fillMaxSize()
        )
        // 2. Верхний блок: Название и прогресс-бар
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

            Spacer(modifier = Modifier.height(8.dp))

            // Прогресс бар
            val progress = if (state.totalPointsCount > 0) state.completedPointsCount.toFloat() / state.totalPointsCount else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                color = Color(0xFF27AE60),
                trackColor = Color.LightGray
            )
        }

        // 3. Кнопка центрирования геолокации
        FloatingActionButton(
            onClick = { onIntent(RouteIntent.CenterOnUser) },
            containerColor = Color.White,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 290.dp, end = 16.dp)
                .size(48.dp)
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Моя геопозиция")
        }

        // 4. Нижняя панель с данными текущей PointModel
        state.currentPoint?.let { currentPoint ->
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Блок описания текущей локации
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFF9E6EA), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentPoint.position.toString(), // Используем position из модели
                                color = burgundyColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(
                                text = currentPoint.name, // Используем name из модели
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currentPoint.address} • радиус триггера ${currentPoint.radiusMeters}м",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }

                    // Блок Аудиоплеера
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val hasAudio = currentPoint.audioUrl.firstOrNull() != null

                            IconButton(
                                onClick = { onIntent(RouteIntent.TogglePlayPause) },
                                enabled = hasAudio
                            ) {
                                Icon(
                                    imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Воспроизведение",
                                    tint = if (hasAudio) Color.Black else Color.LightGray
                                )
                            }

                            Text(
                                text = "0:00 / 1:23",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (hasAudio) Color.Black else Color.LightGray
                            )

                            Slider(
                                value = state.audioProgress,
                                onValueChange = {},
                                modifier = Modifier.weight(1.0F),
                                enabled = hasAudio,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.DarkGray,
                                    activeTrackColor = Color.DarkGray
                                )
                            )

                            IconButton(onClick = { onIntent(RouteIntent.SetAutoplaySheetVisible(true)) }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Настройки", tint = Color.Black)
                            }
                        }
                    }

                    // Кнопка перехода к следующей точке
                    Button(
                        onClick = {
                            onIntent(RouteIntent.CompletePoint(currentPoint.id))
                            onIntent(RouteIntent.NextPoint)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = burgundyColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Далее", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }

    // BottomSheet Автовоспроизведения
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

    // Экран со списком всех точек (Таймлайн)
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