package ru.sibfu.openkras.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun AudioPlayerComponent(
    audioUrl: String?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasAudio = !audioUrl.isNullOrEmpty()

//    if (hasAudio){
//        val correctedUrl = audioUrl.replace("localhost:9000", "10.0.2.2:9000")
//            .replace("127.0.0.1:9000", "10.0.2.2:9000")
//    }
    val sliderProgress = if (durationMs > 0) currentPositionMs.toFloat() / durationMs else 0f

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = Color(0xFFF5F5F5)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Кнопка Play/Pause
            IconButton(
                onClick = onTogglePlay,
                enabled = hasAudio
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Воспроизведение",
                    tint = if (hasAudio) Color.Black else Color.LightGray
                )
            }

            Text(
                text = "${formatTime(currentPositionMs)} / ${formatTime(durationMs)}",
                style = MaterialTheme.typography.bodySmall,
                color = if (hasAudio) Color.Black else Color.LightGray
            )

            // Ползунок трека
            Slider(
                value = sliderProgress,
                onValueChange = onSeek,
                modifier = Modifier.weight(1.0F).height(8.dp),
                enabled = hasAudio,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF8B1A34), // Наш бордовый акцент
                    activeTrackColor = Color(0xFF8B1A34),
                    inactiveTrackColor = Color.LightGray.copy(alpha = 0.5f)
                )
            )

            // Кнопка настроек (Автовоспроизведение)
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.MoreVert, contentDescription = "Настройки", tint = Color.Black)
            }
        }
    }
}

// Хелпер для форматирования миллисекунд в mm:ss
@SuppressLint("DefaultLocale")
private fun formatTime(milliseconds: Long): String {
    if (milliseconds <= 0) return "0:00"
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}