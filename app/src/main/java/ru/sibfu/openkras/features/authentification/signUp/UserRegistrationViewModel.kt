package ru.sibfu.openkras.features.authentification.signUp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.sibfu.domain.usecase.authentificationUseCase.signUpUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import kotlin.onSuccess
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.sibfu.openkras.features.authentification.signIn.LoginEffect


@HiltViewModel
class UserRegistrationViewModel @Inject constructor(
    private val signUpUseCase: signUpUseCase,
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

            UserRegistrationIntent.LoginClicked -> _effect.trySend(UserRegistrationEffect.NavigateToLogin)
        }
    }

    private fun register() {
        val currentState = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            signUpUseCase(
                currentState.name, currentState.email,
                password = currentState.password
            )
                .onSuccess {
                    _effect.send(UserRegistrationEffect.NavigateToOTP(currentState.email))
                }
                .onFailure { exc ->
                    _state.update { it.copy(isLoading = false, error = exc.message) }
                }
        }
    }
}