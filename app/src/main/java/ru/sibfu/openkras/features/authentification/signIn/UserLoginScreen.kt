package ru.sibfu.openkras.features.authentification.signIn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.sibfu.openkras.ui.theme.ThemePreviews

@Composable
fun UserLoginScreen(
    snackbarHostState: SnackbarHostState,
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
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }

                is LoginEffect.NavigateToRegister -> onNavigateToRegistration()
            }
        }
    }

    UserLoginScreenContent(
        state = state,
        onIntent = viewModel::handleIntent
    )
}
@ThemePreviews
@Composable
fun UserLoginScreenContent(
    state: UserLoginState = UserLoginState(),
    onIntent: (UserLoginIntent) -> Unit = {},
){
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Start),
            text = "Войти",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.email,
            onValueChange = { onIntent(UserLoginIntent.EmailChange(it)) },
            label = { Text("E-mail") },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFEBEBEB),
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            isError = state.email.isNotEmpty() && !state.isEmailValid,
            supportingText = {
                if (state.email.isNotEmpty() && !state.isEmailValid) {
                    Text("Некорректный адрес")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { onIntent(UserLoginIntent.PasswordChange(it)) },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFEBEBEB),
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            visualTransformation =
                if (isPasswordVisible){
                    VisualTransformation.None
                }else{
                    PasswordVisualTransformation()
                },
            supportingText = {
                if (state.password.isNotEmpty() && state.error != null) {
                    Text("Неверный пароль")
                }
            },
            isError = state.error != null
        )

        if (state.error != null) {
            Text(text = state.error, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onIntent(UserLoginIntent.LoginClicked) },
            enabled = state.isLoginEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),

        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else{
                Text(
                    text = "Войти",
                    fontSize = 16.sp,
                    color = if (state.isLoginEnabled){
                        Color.White
                    }else{
                        Color(0xFFC2C2C2)
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = { onIntent(UserLoginIntent.RegisterClicked) }
        ) {
            Text(
                text = "Зарегистрироваться",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xff141414)
            )
        }
    }
}