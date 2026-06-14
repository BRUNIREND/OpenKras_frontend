package ru.sibfu.openkras.features.authentification.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.sibfu.domain.usecase.authentificationUseCase.SignUpUseCase
import ru.sibfu.domain.usecase.exception.NetworkResult
import javax.inject.Inject


@HiltViewModel
class UserRegistrationViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(UserRegistrationState())
    val state: StateFlow<UserRegistrationState> = _state.asStateFlow()

    private val _effect = Channel<UserRegistrationEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: UserRegistrationIntent) {
        when (intent) {
            is UserRegistrationIntent.EmailChange -> {
                _state.update { it.copy(email = intent.email, error = null) }
            }
            is UserRegistrationIntent.PasswordChange -> {
                _state.update { it.copy(password = intent.password, error = null) }
            }

            is UserRegistrationIntent.Register -> register()
            is UserRegistrationIntent.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = intent.confirmPassword, error = null) }
            }
            is UserRegistrationIntent.ErrorDismissed -> _state.update { it.copy(error = null) }
            is UserRegistrationIntent.NameChange -> {
                _state.update { it.copy(name = intent.name, error = null) }
            }

            is UserRegistrationIntent.LoginClicked -> _effect.trySend(UserRegistrationEffect.NavigateToLogin)
        }
    }

    private fun register() {
        val currentState = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
//            Log.d(
//                TAG,
//                "email: ${currentState.email}\n" +
//                        "name: ${currentState.name}\n" +
//                        "password: ${currentState.password}"
//            )
            when (val result =
                signUpUseCase(
                    email = currentState.email,
                    name = currentState.name,
                    password = currentState.password
                )

            ) {
                is NetworkResult.Success -> {

                    _effect.send(UserRegistrationEffect.NavigateToOTP(currentState.email))
                    _state.update { it.copy(isLoading = false) }
                }
                is NetworkResult.Error -> {
                    // Показываем текст ошибки, который прислал FastAPI
                    _effect.send(UserRegistrationEffect.ShowSnackbar(result.message))
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }

                is NetworkResult.Exception -> {
                    // Ошибка сети (нет интернета)
                    val text = "Проверьте подключение к интернету"
                    _effect.send(UserRegistrationEffect.ShowSnackbar(text))
                    _state.update { it.copy(isLoading = false, error = "Проверьте подключение к интернету") }
                }
            }
        }
    }
}