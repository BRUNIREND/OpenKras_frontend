package ru.sibfu.openkras.features.authentification.signIn

import android.util.Log
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
import ru.sibfu.domain.usecase.authentificationUseCase.SignInUseCase
import ru.sibfu.domain.usecase.exception.NetworkResult
import javax.inject.Inject

@HiltViewModel
class UserLoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(UserLoginState())
    val state: StateFlow<UserLoginState> = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: UserLoginIntent) {
        when (intent) {
            is UserLoginIntent.EmailChange -> {
                _state.update { it.copy(email = intent.email, error = null) }
            }
            is UserLoginIntent.PasswordChange -> {
                _state.update { it.copy(password = intent.password, error = null) }
            }
            is UserLoginIntent.LoginClicked -> login()

            is UserLoginIntent.ErrorDismissed -> _state.update { it.copy(error = null) }
            is UserLoginIntent.RegisterClicked -> _effect.trySend(LoginEffect.NavigateToRegister)
        }
    }

    private fun login() {
        val currentState = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when (val result =
                signInUseCase(
                    email = currentState.email,
                    password = currentState.password
                )
            ) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false, error = null) }
                    _effect.send(LoginEffect.NavigateToMain)
                }
                is NetworkResult.Error -> {
                    // Показываем текст ошибки, который прислал FastAPI
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.Exception -> {
                    // Ошибка сети (нет интернета)
                    Log.d("Authorization_DEBUG", result.e.toString())
                    _state.update { it.copy(isLoading = false, error = "Проверьте подключение к интернету") }
                }
            }
        }
    }
}