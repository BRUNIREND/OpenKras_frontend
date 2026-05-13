package ru.sibfu.openkras.features.authentification.signUp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.sibfu.openkras.features.authentification.signIn.UserLoginIntent

@Composable
fun UserRegistrationScreen(
    viewModel: UserRegistrationViewModel = hiltViewModel(),
    onNavigateToOtp: (email: String) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is UserRegistrationEffect.NavigateToOTP -> onNavigateToOtp(effect.email)
                is UserRegistrationEffect.NavigateToLogin -> onNavigateToLogin()
                is UserRegistrationEffect.ShowSnackbar -> { /* Show Snackbar */ }
            }
        }
    }

    Column(
    modifier = Modifier
    .fillMaxSize()
    .padding(24.dp)
    .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Регистрация", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Поле Имя
        OutlinedTextField(
            value = state.name,
            onValueChange = { viewModel.handleIntent(UserRegistrationIntent.NameChange(it)) },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Поле Email
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.handleIntent(UserRegistrationIntent.EmailChange(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Поле Пароль
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.handleIntent(UserRegistrationIntent.PasswordChange(it)) },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = state.password.isNotEmpty() && !state.isPasswordValid,
            supportingText = {
                if (state.password.isNotEmpty() && !state.isPasswordValid) {
                    Text("Мин. 8 символов, разный регистр")
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Подтверждение пароля
        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = { viewModel.handleIntent(UserRegistrationIntent.ConfirmPasswordChanged(it)) },
            label = { Text("Подтвердите пароль") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = state.confirmPassword.isNotEmpty() && !state.passwordsMatch,
            supportingText = {
                if (state.confirmPassword.isNotEmpty() && !state.passwordsMatch) {
                    Text("Пароли не совпадают")
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.handleIntent(UserRegistrationIntent.Register) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isRegisterEnabled
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center as Alignment.Vertical))
            } else {
                Text("Зарегистрироваться")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { viewModel.handleIntent(UserRegistrationIntent.LoginClicked) }
        ) {
            Text("Уже есть аккаунт? Войти")
        }
    }
}