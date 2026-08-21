package ru.sibfu.openkras.features.user.sideScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsageConditionsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        PolicySection(
            title = "Общие правила",
            content = "Используя приложение OpenKras, вы соглашаетесь соблюдать правила безопасности дорожного движения во время прохождения экскурсий. Будьте внимательны на дорогах."
        )
        PolicySection(
            title = "Интеллектуальная собственность",
            content = "Весь аудиовизуальный контент (тексты, аудио, фотографии) защищен авторским правом. Копирование и распространение контента без разрешения правообладателя запрещено."
        )
        PolicySection(
            title = "Ограничение ответственности",
            content = "Разработчики не несут ответственности за травмы или ущерб, полученные пользователем в процессе физического следования по маршрутам."
        )
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8B1A34)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 20.sp,
            textAlign = TextAlign.Justify
        )
    }
}