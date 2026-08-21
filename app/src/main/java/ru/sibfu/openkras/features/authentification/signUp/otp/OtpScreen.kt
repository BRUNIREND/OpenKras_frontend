package ru.sibfu.openkras.features.authentification.signUp.otp

import android.annotation.SuppressLint
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.sibfu.openkras.ui.theme.OpenKrasTheme
import ru.sibfu.openkras.ui.theme.ThemePreviews

@Composable
fun OtpScreen(
    onNavigateToMain: () -> Unit,
    email: String,
    viewModel: OtpViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect){
                is OtpEffect.NavigateToMain -> onNavigateToMain()
                is OtpEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message
                    )
                }
            }
        }

    }

    OtpScreenContent(
        state = state,
        onIntent = { viewModel.handleIntent(it) }
    )
}

@SuppressLint("DefaultLocale")
@ThemePreviews
@Composable
fun OtpScreenContent(
    state: OtpState = OtpState(),
    onIntent: (OtpIntent) -> Unit = {}
){
    OpenKrasTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 67.dp)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.Start),
                text = "Введите код из письма",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .align(Alignment.Start),
                text = "Мы отправили код подтверждения на\nвашу почту ${state.email}",
                fontSize = 16.sp,
                color = Color(0xFF5E5E5E)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. Скрытый инпут, который принимает фокус и ввод
                BasicTextField(
                    value = state.code,
                    onValueChange = { onIntent(OtpIntent.CodeChanged(it)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.01f) // Делаем невидимым, но кликабельным
                )
                // 2. Визуальные ячейки, которые просто отображают буквы из стейта
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until state.codeLength) {
                        val char = state.code.getOrNull(i)?.toString() ?: ""
                        val isFocused = state.code.length == i

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .border(
                                    width = if (isFocused) 2.dp else 1.dp,
                                    color = if (isFocused) MaterialTheme.colorScheme.primary else Color(0xFFEBEBEB),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char,
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = {
                    onIntent(OtpIntent.VerifyClicked)
                    onIntent(OtpIntent.navigateToAllScreen)
              },
                enabled = state.code.length == 6 && !state.isLoading,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Подтверить", color = Color.White)
                }
            }

            if (state.secondsLeft > 0) {
                val minutes = state.secondsLeft / 60
                val seconds = state.secondsLeft % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)

                Text(
                    text = "Отправить код повторно через $timeString",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                TextButton(
                    onClick = { onIntent(OtpIntent.ResendClicked) },
                    enabled = state.isResendEnabled
                ) {
                    Text(
                        text = "Отправить код повторно",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}