package ru.sibfu.openkras.features.routeNavigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import ru.sibfu.domain.PointModel
import ru.sibfu.openkras.R
import ru.sibfu.openkras.ui.theme.AudioPlayerComponent

@Composable
fun CurrentPointDetailsSheet(
    point: PointModel,
    isFirstPoint: Boolean,
    isPlaying: Boolean,
    isLastPoint: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    burgundyColor: Color,
    lightGrayBackground: Color,
    onIntent: (RouteIntent) -> Unit,
    onImageClick: (Int) -> Unit,
    sheetProgress: Float // 0.0 (Свернуто) -> 1.0 (Развернуто)
) {
    val isExpanded = sheetProgress > 0.2f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .background(Color.White)
    ) {
        if (isExpanded && point.images.isNotEmpty()) {
            FullWidthImageCarousel(
                images = point.images.filterNotNull(),
                onImageClick = onImageClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xffFCE9EC),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "${point.position}", color = burgundyColor, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = point.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = point.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = point.description,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        }

        Surface(
            color = Color.White,
            tonalElevation = if (isExpanded) 8.dp else 0.dp,
            shadowElevation = if (isExpanded) 8.dp else 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                AudioPlayerComponent(
                    audioUrl = point.audioUrl.firstOrNull(),
                    isPlaying = isPlaying,
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    onTogglePlay = { onIntent(RouteIntent.TogglePlayPause) },
                    onSeek = { onIntent(RouteIntent.SeekAudio(it)) },
                    onSettingsClick = { onIntent(RouteIntent.SetAutoplaySheetVisible(true)) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isFirstPoint) {
                        Button(
                            onClick = { onIntent(RouteIntent.PreviousPoint) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = lightGrayBackground)
                        ) {
                            Text("Назад", color = Color.Black)
                        }
                    }

                    Button(
                        onClick = {
                            if (isLastPoint) onIntent(RouteIntent.CompleteRoute)
                            else onIntent(RouteIntent.NextPoint)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = burgundyColor)
                    ) {
                        Text(if (isLastPoint) "Завершить" else "Далее", color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullWidthImageCarousel(
    images: List<String>,
    onImageClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val correctedUrl = images[page]?.replace("localhost:9000", "10.0.2.2:9000")
                ?.replace("127.0.0.1:9000", "10.0.2.2:9000")
            AsyncImage(
                model = correctedUrl,
                placeholder = painterResource(R.drawable.img_mock),
                error = painterResource(R.drawable.img_mock_error),
                contentDescription = "Фото локации",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onImageClick(page) }
            )
        }

        // Точки-индикаторы страниц поверх фото (показываем, только если картинок больше одной)
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(images.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                    Box(
                        modifier = Modifier
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(6.dp)
                    )
                }
            }
        }
    }
}

