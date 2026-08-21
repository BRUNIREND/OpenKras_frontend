package ru.sibfu.openkras.features.user.sideScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        PolicySection(
            title = "1. Сбор данных",
            content = "Приложение запрашивает доступ к вашему местоположению исключительно для корректной работы навигации по маршруту экскурсии. Мы не передаем данные о ваших перемещениях третьим лицам."
        )
        PolicySection(
            title = "2. Использование микрофона",
            content = "Приложение не использует микрофон и не записывает аудио без вашего явного согласия."
        )
        PolicySection(
            title = "3. Хранение данных",
            content = "Вся информация о пройденных маршрутах хранится локально на вашем устройстве или в защищенном облачном хранилище вашего аккаунта."
        )
        PolicySection(
            title = "4. Изменения",
            content = "Мы оставляем за собой право обновлять политику конфиденциальности. Актуальная версия всегда доступна в этом разделе."
        )
    }
}