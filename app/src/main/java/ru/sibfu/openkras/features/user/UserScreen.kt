package ru.sibfu.openkras.features.user

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel


@Composable
fun UserScreen(
    onNavigateToAboutApp: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToUsageCondition: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: UserScreenViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
){
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect){
                ScreenEffect.NavigateToAboutApp -> onNavigateToAboutApp()
                ScreenEffect.NavigateToPrivacyPolicy -> onNavigateToPrivacyPolicy()
                ScreenEffect.NavigateToUsageCondition -> onNavigateToUsageCondition()
                is ScreenEffect.ShowSnackbar -> TODO()
                ScreenEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    UserScreenContent(
        state = state,
        onIntent = viewModel::handleIntent
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreenContent(
    modifier: Modifier = Modifier,
    state: UserScreenState = UserScreenState(),
    onIntent: (UserScreenIntent) -> Unit = {},
){
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        UserProfileData(
            name = state.name,
            email = state.email,
            modifier = modifier
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Outlined.InsertDriveFile,
                text = "Условие использования",
                onClick = {onIntent(UserScreenIntent.UsageConditionClicked)}
            )
            ProfileMenuItem(
                icon = Icons.Outlined.Lock,
                text = "Политика конфиденциальности",
                onClick = {onIntent(UserScreenIntent.PrivacyPolicyClicked)}

            )
            ProfileMenuItem(
                icon = Icons.Outlined.ErrorOutline,
                text = "О приложении",
                onClick = {onIntent(UserScreenIntent.AboutAppClicked)}
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Кнопка выхода с цветом ошибки из темы
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Outlined.Logout,
                text = "Выйти из аккаунта",
                textColor = MaterialTheme.colorScheme.error,
                iconTint = MaterialTheme.colorScheme.error,
                onClick = {showBottomSheet = true}
            )
            if (showBottomSheet){
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 40.dp, top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Заголовок шторки
                        Text(
                            text = "Выход из аккаунта",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Подтекст
                        Text(
                            text = "Вы уверены, что хотите выйти? Чтобы войти снова, потребуется ввести данные аккаунта.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Строка с кнопками действий
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Кнопка ОТМЕНЫ (занимает 50% ширины)
                            OutlinedButton(
                                onClick = { showBottomSheet = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Отмена")
                            }

                            // Кнопка ПОДТВЕРЖДЕНИЯ (занимает 50% ширины, подсвечена цветом ошибки)
                            Button(
                                onClick = {
                                    showBottomSheet = false
                                    onIntent(UserScreenIntent.OnLogoutClick)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                )
                            ) {
                                Text(text = "Выйти")
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun UserProfileData(
    modifier: Modifier = Modifier,
    name: String = "John Doe",
    email: String = "john.mckinley@examplepetstore.com",
){
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Аватар",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(28.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = email,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = "Перейти",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun ProfileScreenPreview() {
//    OpenKrasTheme {
//        UserScreenContent()
//    }
//}