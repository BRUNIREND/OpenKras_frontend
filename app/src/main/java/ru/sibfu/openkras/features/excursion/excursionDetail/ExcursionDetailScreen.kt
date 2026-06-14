package ru.sibfu.openkras.features.excursion.excursionDetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.sibfu.domain.ExcursionDetailModel
import ru.sibfu.domain.PointModel
import ru.sibfu.openkras.R
import ru.sibfu.openkras.features.excursion.ImageLoader
import ru.sibfu.openkras.features.excursion.InfoItem
import ru.sibfu.openkras.ui.theme.ThemePreviews

@Composable
fun ExcursionDetailScreen(
    excursionId: Int,
    onBackClick: () -> Unit,
    viewModel: ExcursionDetailViewModel = hiltViewModel(),
    onExcursionStart: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ExcursionDetailEffect.NavigateToRoute -> {
                    onExcursionStart(effect.excursionId)
                }
            }
        }
    }

    LaunchedEffect(excursionId) {
        viewModel.loadExcursionDetails(excursionId)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            // 1. Состояние загрузки (если данных еще нет и isLoading = true)
            state.isLoading && state.data == null -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF8B1A34)
                )
            }

            // 2. Состояние ошибки (если есть текст ошибки и нет данных)
            state.error != null && state.data == null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadExcursionDetails(excursionId) }) {
                        Text("Повторить")
                    }
                }
            }

            // 3. Успешное состояние (данные загружены)
            state.data != null -> {
                ExcursionDetailContent(
                    excursion = state.data!!,
                    isFavorite = state.isFavorite,
                    onBackClick = onBackClick,
                    onIntent = { intent -> viewModel.handleIntent(intent) },
                )

                // Дополнительно: если данные есть, но идет фоновое обновление (рефреш)
                if (state.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                        color = Color(0xFF8B1A34)
                    )
                }
            }
        }
    }
}



val excursionDetailModel = ExcursionDetailModel(
    id = 1,
    title = "dsfafasdfsdafsdafsad",
    description = "dsafffffffffdsafdsafdsafdsafsadfdasfsdafsdafsdafdsafdsafdsafdsafdsafdsa",
    duration = 123,
    distance = 123.0,
    coverUrl = "",
    images = listOf("https://img.freepik.com/free-photo/view-funny-animal_23-2151098313.jpg?semt=ais_hybrid&w=740&q=80"),
    points = listOf(
        PointModel(
            id = 1,
            name = "dsfadsfa",
            description = "asdasddsa",
            address = "sadasddsadsa",
            latitude = 123.0,
            longitude = 123.0,
            radiusMeters = 20,
            audioUrl = listOf("dsfa"),
            images = listOf("asd"),
            position = 1
        ),
        PointModel(
            id = 2,
            name = "dsfadsfa",
            description = "asdasddsa",
            address = "sadasddsadsa",
            latitude = 123.0,
            longitude = 123.0,
            radiusMeters = 20,
            audioUrl = listOf("dsfa"),
            images = listOf("asd"),
            position = 2
        ),
        PointModel(
            id = 3,
            name = "dsfadsfa",
            description = "asdasddsa",
            address = "sadasddsadsa",
            latitude = 123.0,
            longitude = 123.0,
            radiusMeters = 20,
            audioUrl = listOf("dsfa"),
            images = listOf("asd"),
            position = 3
        )
    ),
    categoryId = 1,
    isFavorite = true
)
@ThemePreviews
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExcursionDetailContent(
    excursion: ExcursionDetailModel = excursionDetailModel,
    isFavorite: Boolean = true,
    onBackClick: () -> Unit = {},
    onIntent: (ExcursionDetailIntent) -> Unit = {}
) {
    val burgundyColor = Color(0xFF8B1A34)

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 16.dp,
                color = Color.White
            ) {
                Button(
                    onClick = { onIntent(ExcursionDetailIntent.StartRoute(excursionId = excursion.id)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = burgundyColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Начать маршрут", style = MaterialTheme.typography.titleMedium, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // 1. Блок с картинками и кнопками поверх них
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)) {

                    val images = excursion.images
                    val pagerState = rememberPagerState(pageCount = { images.size })

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val url = images[page]
                        ImageLoader(url)
                    }

                    // Индикаторы страниц
                    if (images.size > 1) {
                        Row(
                            Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(images.size) { iteration ->
                                val color = if (pagerState.currentPage == iteration) burgundyColor else Color.LightGray
                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .size(8.dp)
                                )
                            }
                        }
                    }

                    // Кнопка НАЗАД
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart)
                            .size(40.dp)
                            .background(burgundyColor, CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
                    }

                    // Кнопки Избранное и Скачать
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopEnd),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (isFavorite){
                                    onIntent(ExcursionDetailIntent.onRemoveExcursionFromFavorites(excursion.id))
                                }else {
                                    onIntent(ExcursionDetailIntent.onAddExcursionToFavorites(excursion.id))
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "В избранное",
                                tint = if (isFavorite) burgundyColor else Color.Black
                            )
                        }
                        IconButton(
                            onClick = { onIntent(ExcursionDetailIntent.DownloadLocally) },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(Icons.Filled.Download, contentDescription = "Скачать", tint = Color.Black)
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = excursion.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Длительность и Длина
                    Row (
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),

                        ){
                        InfoItem(iconId = R.drawable.ic_clock, text = "${excursion.duration} мин")
                        InfoItem(iconId = R.drawable.ic_path, text = "${excursion.distance} км")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "О маршруте", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = excursion.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Остановки", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            itemsIndexed(excursion.points) { index, point ->
                StopItem(
                    number = index + 1,
                    title = point.name,
                    address = point.address,
                    burgundyColor = burgundyColor
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun StopItem(
    number: Int,
    title: String,
    address: String,
    burgundyColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Кружочек с номером
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(burgundyColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Текстовая информация
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = Color.Black)
            Text(text = address, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}