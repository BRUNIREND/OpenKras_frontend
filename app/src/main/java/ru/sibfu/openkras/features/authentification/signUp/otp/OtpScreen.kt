package ru.sibfu.openkras.features.authentification.signUp.otp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun OtpScreen(
    email: String,
    viewModel: OtpViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Введите код, отправленный на $email")

        OutlinedTextField(
            value = state.code,
            onValueChange = { if (it.length <= 6) viewModel.handleIntent(OtpIntent.CodeChanged(it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Код из 6 цифр") }
        )

        Button(
            onClick = { viewModel.handleIntent(OtpIntent.VerifyClicked) },
            enabled = state.code.length == 6 && !state.isLoading
        ) {
            Text("Подтвердить")
        }
    }
}