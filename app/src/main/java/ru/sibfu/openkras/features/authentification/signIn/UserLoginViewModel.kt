package ru.sibfu.openkras.features.authentification.signIn

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.sibfu.domain.usecase.authentificationUseCase.signInUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.sibfu.openkras.features.excursion.ExcursionState
import javax.inject.Inject

@HiltViewModel
class UserLoginViewModel @Inject constructor(
    private val signInUseCase: signInUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(UserLoginState())
    val state: StateFlow<UserLoginState> = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: UserLoginIntent) {
        when (intent) {
            is UserLoginIntent.EmailChange -> {
                _state.update { it.copy(username = intent.email, error = null) }
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

            signInUseCase(currentState.username, currentState.password)
                .onSuccess {
                    _effect.send(LoginEffect.NavigateToMain)
                }
                .onFailure { exc ->
                    _state.update { it.copy(isLoading = false, error = exc.message) }
                }
        }
    }

    private fun validateUserData(inputString: String){

    }
}