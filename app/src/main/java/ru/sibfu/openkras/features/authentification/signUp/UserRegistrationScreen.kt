package ru.sibfu.openkras.features.authentification.signUp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
fun UserRegistrationScreen(
    viewModel: UserRegistrationViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    onNavigateToOtp: (email: String) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is UserRegistrationEffect.NavigateToOTP -> onNavigateToOtp(effect.email)
                is UserRegistrationEffect.NavigateToLogin -> onNavigateToLogin()
                is UserRegistrationEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    UserRegistrationScreenContent(
        state = state,
        onIntent = viewModel::handleIntent
    )
}
@ThemePreviews
@Composable
fun UserRegistrationScreenContent(
    state: UserRegistrationState = UserRegistrationState(),
    onIntent: (UserRegistrationIntent) -> Unit = {},
){
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Start),
            text = "Зарегистрироваться",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Поле Имя
        OutlinedTextField(
            value = state.name,
            onValueChange = { newName ->
                onIntent(UserRegistrationIntent.NameChange(newName))
            },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFEBEBEB),
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            label = { Text("Имя") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Поле Email
        OutlinedTextField(
            value = state.email,
            onValueChange = { newEmail ->
                onIntent(UserRegistrationIntent.EmailChange(newEmail))
            },
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

        Spacer(modifier = Modifier.height(12.dp))

        // Поле Пароль
        OutlinedTextField(
            value = state.password,
            onValueChange = { newPassword ->
                onIntent(UserRegistrationIntent.PasswordChange(newPassword))
            },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFEBEBEB),
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            visualTransformation = if (isPasswordVisible){
                VisualTransformation.None
            }else{
                PasswordVisualTransformation()
            },
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
            onValueChange = { newConfirmPassword ->
                onIntent(UserRegistrationIntent.ConfirmPasswordChanged(newConfirmPassword))
            },
            label = { Text("Подтвердите пароль") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFEBEBEB),
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            visualTransformation = if (isPasswordVisible){
                VisualTransformation.None
            }else{
                PasswordVisualTransformation()
            },
            isError = state.confirmPassword.isNotEmpty() && !state.passwordsMatch,
            supportingText = {
                if (state.confirmPassword.isNotEmpty() && !state.passwordsMatch) {
                    Text("Пароли не совпадают")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onIntent(UserRegistrationIntent.Register) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            enabled = !state.isLoading && state.isRegisterEnabled,
            shape = RoundedCornerShape(14.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Зарегистрироваться",
                    fontSize = 16.sp,
                    color = if (state.isRegisterEnabled){
                        Color.White
                    }else{
                        Color(0xFFC2C2C2)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = { onIntent(UserRegistrationIntent.LoginClicked) }
        ) {
            Text(
                text = "Войти",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xff141414)
            )
        }
        Spacer(Modifier.height(87.dp))
//        Text("Продолжая регистрацию, вы принимаете условия \n" +
//                "и политику конфиденциальности")
    }
}