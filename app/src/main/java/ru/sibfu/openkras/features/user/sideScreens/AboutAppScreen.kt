package ru.sibfu.openkras.features.user.sideScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.sibfu.openkras.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppScreen() {
    val burgundyColor = Color(0xFF8B1A34)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = "Открой Красноярск",
            style = MaterialTheme.typography.headlineMedium,
            color = burgundyColor,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Версия 1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Открой Красноярск — это ваш интерактивный гид по культурному и историческому наследию Красноярска. Мы создаем уникальные аудиоэкскурсии, которые оживляют историю города прямо в вашем смартфоне.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Наша миссия — сделать историю доступной и интересной для каждого жителя и гостя нашего города. Приложение разработано при поддержке СФУ.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "© 2026 СФУ. Все права защищены.",
            style = MaterialTheme.typography.labelSmall,
            color = Color.LightGray,
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}
