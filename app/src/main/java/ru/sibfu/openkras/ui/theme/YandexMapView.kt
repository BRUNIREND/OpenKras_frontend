package ru.sibfu.openkras.ui.theme

import android.content.Context
import android.graphics.PointF
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.FitnessOptions
import com.yandex.mapkit.transport.masstransit.PedestrianRouter
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.RouteOptions
import com.yandex.mapkit.transport.masstransit.Session
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.runtime.ui_view.ViewProvider
import ru.sibfu.domain.PointModel
import ru.sibfu.openkras.R

@Composable
fun YandexMapView(
    modifier: Modifier = Modifier,
    points: List<PointModel>,
    currentPointIndex: Int,
    userLatitude: Double? = 56.008284,
    userLongitude: Double? = 92.767369,
    onPointClick: (PointModel) -> Unit,
    cameraTarget: Point? = null
) {
    Log.d("YandexMapView", "Recomposing/Updating parameters")

    val context = LocalContext.current

    val mapView = remember(context) {
        MapView(context).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }
    val map = remember(mapView) { mapView.mapWindow.map }

    var routeCollection by remember { mutableStateOf<MapObjectCollection?>(null) }
    var userCollection by remember { mutableStateOf<MapObjectCollection?>(null) }

    var isMapReady by remember { mutableStateOf(false) }
    var pedestrianRouter by remember { mutableStateOf<PedestrianRouter?>(null) }
    var routeSession by remember { mutableStateOf<Session?>(null) }
    var isRouteBuilt by remember(points) { mutableStateOf(false) }

    var pinCollection by remember { mutableStateOf<MapObjectCollection?>(null) }

    val routeListener = remember {
        object : Session.RouteListener {
            override fun onMasstransitRoutes(routes: MutableList<Route>) {
                try {
                    routes.firstOrNull()?.let { route ->
                        mapView.post {
                            try {
                                // Обращаемся к коллекции через безопасный вызов (?.)
                                routeCollection?.clear()
                                routeCollection?.addPolyline(route.geometry)?.apply {
                                    strokeWidth = 4f
                                    setStrokeColor("#8B1A34".toColorInt())
                                }
                            } catch (e: Exception) {
                                Log.e("YandexMapView", "Error adding route: ${e.message}")
                            }
                        }
                        isRouteBuilt = true
                    }
                } catch (e: Exception) {
                    Log.e("YandexMapView", "Error in onMasstransitRoutes: ${e.message}")
                }
            }

            override fun onMasstransitRoutesError(error: com.yandex.runtime.Error) {
                Log.e("YandexMapView", "Route error: ${error}")
            }
        }
    }

    DisposableEffect(mapView) {
        try {
            MapKitFactory.getInstance().onStart()
            mapView.onStart()

            routeCollection = map.mapObjects.addCollection()
            userCollection = map.mapObjects.addCollection()
            pedestrianRouter = TransportFactory.getInstance().createPedestrianRouter()
            pinCollection = map.mapObjects.addCollection()

            isMapReady = true
        } catch (e: Exception) {
            Log.e("YandexMapView", "Error starting MapKit: ${e.message}")
        }

        onDispose {
            try {
                isMapReady = false
                pedestrianRouter = null
                routeSession?.cancel()

                routeCollection?.clear()
                userCollection?.clear()

                mapView.onStop()
                MapKitFactory.getInstance().onStop()
            } catch (e: Exception) {
                Log.e("YandexMapView", "Error stopping MapKit: ${e.message}")
            }
        }
    }

    LaunchedEffect(points, userLatitude, userLongitude, isMapReady, pedestrianRouter) {
        if (!isMapReady || pedestrianRouter == null || points.isEmpty() || isRouteBuilt) return@LaunchedEffect

        val userLat = userLatitude ?: return@LaunchedEffect
        val userLon = userLongitude ?: return@LaunchedEffect
        Log.d("YandexMapKit", "Данные прокинули для работы с маршрутом")
        val requestPoints = mutableListOf<RequestPoint>().apply {
            add(RequestPoint(Point(userLat, userLon), RequestPointType.WAYPOINT, null, null, null))
            points.forEach { point ->
                add(RequestPoint(Point(point.latitude, point.longitude), RequestPointType.WAYPOINT, null, null, null))
            }
        }
        Log.d("YandexMapKit", "Начали работать с маршрутом")
        routeSession?.cancel()
        routeSession = null

        try {
            val timeOptions = TimeOptions()
            val routeOptions = RouteOptions(
                FitnessOptions(false, false),
            )

            Log.d("YandexMapView", "Requesting route with ${requestPoints.size} points")

            routeSession = pedestrianRouter!!.requestRoutes(
                requestPoints,
                timeOptions,
                routeOptions,
                routeListener
            )

            Log.d("YandexMapView", "Route request submitted successfully")
        } catch (e: Exception) {
            Log.e("YandexMapView", "Error requesting route: ${e.message}", e)
            isRouteBuilt = false
        }
    }

    // Обновление маркера пользователя
    LaunchedEffect(userLatitude, userLongitude, isMapReady) {
        if (isMapReady && userLatitude != null && userLongitude != null) {
            userCollection?.clear()
            val userPoint = Point(userLatitude, userLongitude)
            userCollection?.addPlacemark(userPoint)?.apply {
                useCompositeIcon()
            }
        }
    }

    // Камера к текущей точке
    LaunchedEffect(currentPointIndex, points, isMapReady) {
        if (points.isNotEmpty() && isMapReady) {
            points.getOrNull(currentPointIndex)?.let { current ->
                try {
                    map.move(
                        CameraPosition(Point(current.latitude, current.longitude), 16.0f, 0.0f, 0.0f),
                        Animation(Animation.Type.SMOOTH, 1.0f),
                        null
                    )
                } catch (e: Exception) {
                    Log.e("YandexMapView", "Camera move error: ${e.message}")
                }
            }
        }
    }

    LaunchedEffect(points, isMapReady) {
        if (isMapReady && pinCollection != null) {
            pinCollection?.clear()
            points.forEachIndexed { index, point ->

                val pinView = createPinView(context, index + 1)

                // 2. Создаем метку в коллекции
                val placemark = pinCollection?.addPlacemark(
                    Point(point.latitude, point.longitude),
                    ViewProvider(pinView)
                )

                placemark?.setIconStyle(IconStyle().apply {
                    anchor = PointF(0.5f, 1.0f) // 0.5 - центр по гор., 1.0 - низ по верт.
                })

                // 5. Клик
                placemark?.addTapListener { _, _ ->
                    onPointClick(point) // Ваш callback для bottomSheet
                    true
                }
            }
        }
    }
    LaunchedEffect(cameraTarget) {
        cameraTarget?.let { target ->
            map.move(
                CameraPosition(target, 16.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 0.5f),
                null
            )
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize()
    )

}

private fun createPinView(context: Context, number: Int): View {
    val view = LayoutInflater.from(context).inflate(R.layout.pin_layout, null)
    val textView = view.findViewById<TextView>(R.id.pin_number)
    textView.text = number.toString()

    textView.setTextColor(android.graphics.Color.WHITE)

    view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)

    return view
}


//private fun createBitmapFromView(view: View): Bitmap {
//    view.measure(
//        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//    )
//    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
//
//    val bitmap = createBitmap(view.measuredWidth, view.measuredHeight)
//    val canvas = Canvas(bitmap)
//    view.draw(canvas)
//    return bitmap
//}