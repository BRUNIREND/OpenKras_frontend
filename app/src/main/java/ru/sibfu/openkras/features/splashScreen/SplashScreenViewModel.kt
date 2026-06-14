package ru.sibfu.openkras.features.splashScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.sibfu.data.repository.core.TokenManager
import ru.sibfu.domain.usecase.authentificationUseCase.GetCurrentUserUseCase
import ru.sibfu.domain.usecase.exception.NetworkResult
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            val token = tokenManager.getAccessToken()
            android.util.Log.d("AUTH_DEBUG", "Токен из DataStore: '$token'")

            if (token == null) {
                _state.value = SplashState(SplashNavigationState.NavigateToLogin)
                return@launch
            }

            val result = getCurrentUserUseCase()

            when (result) {
                is NetworkResult.Success -> {
                    _state.value = SplashState(SplashNavigationState.NavigateToMain)
                }
                is NetworkResult.Error -> {
                    if (result.code == 401 || result.code == 404) {
                        tokenManager.deleteToken()
                        _state.value = SplashState(SplashNavigationState.NavigateToLogin)
                    }
                }

                is NetworkResult.Exception -> {
                    _state.value = SplashState(SplashNavigationState.NavigateToMain)
                }
            }
        }
    }
}