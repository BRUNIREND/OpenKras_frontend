package ru.sibfu.openkras.features.authentification.signUp.otp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.sibfu.domain.usecase.authentificationUseCase.ReSendOtpCodeUseCase
import ru.sibfu.domain.usecase.authentificationUseCase.VerifyOtpUseCase
import ru.sibfu.domain.usecase.exception.NetworkResult
import ru.sibfu.openkras.navigation.AuthScreenGraph
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val reSendOtpCodeUseCase: ReSendOtpCodeUseCase,
) : ViewModel() {


    val args = savedStateHandle.toRoute<AuthScreenGraph.OtpScreen>()
    private val email = args.email
    private val _state = MutableStateFlow(
        value = OtpState(email = email)
    )
    val state = _state.asStateFlow()

    private val _effect = Channel<OtpEffect>()
    val effect = _effect.receiveAsFlow()

    private var timerJob: Job? = null

    private val TIMERDEADLINEKEY = "otp_timer_deadline"

    init {
        val savedDeadline = savedStateHandle.get<Long>(TIMERDEADLINEKEY)
        if (savedDeadline != null && savedDeadline > System.currentTimeMillis()) {
            startCountdown(savedDeadline)
        } else {
            triggerNewTimer()
        }
    }
    fun handleIntent(intent: OtpIntent) {
        when (intent) {
            is OtpIntent.CodeChanged -> {
                if (intent.value.length <= _state.value.codeLength) {
                    _state.update { it.copy(code = intent.value, error = null) }
                }
            }
            is OtpIntent.VerifyClicked -> verify()
            is OtpIntent.ResendClicked -> {
                if (_state.value.isResendEnabled) {
                    sendNewCodeToBackend()
                }
            }
            is OtpIntent.navigateToAllScreen -> viewModelScope.launch {
                _effect.trySend(OtpEffect.NavigateToMain)
            }
        }
    }

    private fun verify() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = verifyOtpUseCase(_state.value.code)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false, error = null) }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.Exception -> {
                    _state.update { it.copy(isLoading = false, error = "Проверьте подключение к интернету") }
                }
            }
        }
    }
    private fun triggerNewTimer() {
        val deadline = System.currentTimeMillis() + 60_000L
        savedStateHandle[TIMERDEADLINEKEY] = deadline
        startCountdown(deadline)
    }

    private fun startCountdown(deadlineTimestamp: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val current = System.currentTimeMillis()
                val remainingSeconds = ((deadlineTimestamp - current) / 1000).toInt()

                if (remainingSeconds <= 0) {
                    _state.update { it.copy(secondsLeft = 0) }
                    savedStateHandle.remove<Long>(TIMERDEADLINEKEY)
                    break
                } else {
                    _state.update { it.copy(secondsLeft = remainingSeconds) }
                }
                delay(1000)
            }
        }
    }
    private fun sendNewCodeToBackend() {
        triggerNewTimer()
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result =
                reSendOtpCodeUseCase(email = email)) {
                is NetworkResult.Success -> {
                    _effect.send(OtpEffect.ShowSnackbar("Код успешно отправлен"))
                    _state.update { it.copy(isLoading = false) }
                }
                is NetworkResult.Error -> {
                    // Показываем текст ошибки, который прислал FastAPI
                    _effect.send(OtpEffect.ShowSnackbar(result.message))
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }

                is NetworkResult.Exception -> {
                    // Ошибка сети (нет интернета)
                    val text = "Проверьте подключение к интернету"
                    _effect.send(OtpEffect.ShowSnackbar(text))
                    _state.update { it.copy(isLoading = false, error = "Проверьте подключение к интернету") }
                }
            }
        }
    }
}