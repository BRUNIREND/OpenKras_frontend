package ru.sibfu.openkras.features.authentification.signUp.otp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.sibfu.domain.interfaces.IAuthRepository
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val repository: IAuthRepository, // Change to useCase
    savedStateHandle: SavedStateHandle // Для получения email из навигации
) : ViewModel() {

    // Извлекаем email, переданный при навигации
    private val email: String = savedStateHandle["email"] ?: ""

    private val _state = MutableStateFlow(OtpState(email = email))
    val state = _state.asStateFlow()

    private val _effect = Channel<OtpEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: OtpIntent) {
        when (intent) {
            is OtpIntent.CodeChanged -> _state.update { it.copy(code = intent.value) }
            is OtpIntent.VerifyClicked -> verify()
        }
    }

    private fun verify() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.verifyOtp(email, _state.value.code)
                .onSuccess {
                    _effect.send(OtpEffect.NavigateToMain)
                }
                .onFailure { exc ->
                    _state.update { it.copy(isLoading = false, error = exc.message) }
                }
        }
    }
}