package ru.sibfu.openkras.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import ru.sibfu.domain.PointModel


@Composable
fun YandexMapView(
    points: List<PointModel>,
    currentPointIndex: Int,
    userLocation: Pair<Double, Double>?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Создаем экземпляр MapView и сохраняем его в remember
    val mapView = remember { MapView(context) }

    // Синхронизация жизненного цикла MapKit с Compose жизненным циклом
    DisposableEffect(mapView) {
        MapKitFactory.getInstance().onStart()
        mapView.onStart()

        onDispose {
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }

    // Слушатель изменений точек и текущего шага навигации
    LaunchedEffect(points, currentPointIndex, userLocation) {
        val map = mapView.mapWindow.map
        map.mapObjects.clear() // Сбрасываем старые маркеры перед перерисовкой

        val routePoints = mutableListOf<Point>()

        // 1. Отрисовка пинов экскурсии
        points.forEach { pointData ->
            val mapPoint = Point(pointData.latitude, pointData.longitude)
            routePoints.add(mapPoint)

            val pinBitmap = createNumberedPinBitmap(context, pointData.position)
            map.mapObjects.addPlacemark(mapPoint).apply {
                setIcon(ImageProvider.fromBitmap(pinBitmap))
                setText(pointData.name)
            }
        }

        // 2. Строим линию маршрута (Polyline) между точками
        if (routePoints.size >= 2) {
            val polyline = Polyline(routePoints)
            map.mapObjects.addPolyline(polyline).apply {
                strokeWidth = 5f
                setStrokeColor("#8B1A34".toColorInt()) // Наш бордовый
            }
        }

        // 3. Добавляем маркер пользователя, если есть координаты
        userLocation?.let {
            val userPoint = Point(it.first, it.second)
            map.mapObjects.addPlacemark(userPoint).apply {
                // В реальном приложении можно подставить синюю стрелочку из ресурсов:
                // setIcon(ImageProvider.fromResource(context, R.drawable.ic_user_navigation))
                useCompositeIcon()
            }
        }

        // 4. Фокусируем камеру на текущую целевую точку
        points.getOrNull(currentPointIndex)?.let { current ->
            map.move(
                CameraPosition(
                    Point(current.latitude, current.longitude),
                    16.0f, // Zoom level
                    0.0f,  // Azimuth
                    0.0f   // Tilt
                )
            )
        }
    }

    // Рендерим стандартный Android View внутри Jetpack Compose layout
    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}