package ru.sibfu.openkras.features.authentification.signIn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun UserLoginScreen(
    viewModel: UserLoginViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit,
    onNavigateToRegistration: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Обработка разовых эффектов
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginEffect.NavigateToMain -> onNavigateToMain()
                is LoginEffect.ShowSnackbar -> {
                    // Покажите Snackbar с сообщением
                }

                is LoginEffect.NavigateToRegister -> onNavigateToRegistration()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.handleIntent(UserLoginIntent.EmailChange(it)) },
            label = { Text("Email") },
            isError = state.error != null
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.handleIntent(UserLoginIntent.PasswordChange(it)) },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            isError = state.error != null
        )

        if (state.error != null) {
            Text(text = state.error!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.handleIntent(UserLoginIntent.LoginClicked) },
            enabled = !state.isLoading && state.username.isNotBlank() && state.password.isNotBlank()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center as Alignment.Vertical))
            } else {
                Text("Войти")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { viewModel.handleIntent(UserLoginIntent.RegisterClicked) }
        ) {
            Text("Нет аккаунта? Зарегистрироваться")
        }
    }
}