package ru.sibfu.openkras.features.user

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
import ru.sibfu.domain.usecase.authentificationUseCase.GetCurrentUserUseCase
import ru.sibfu.domain.usecase.authentificationUseCase.LogoutUseCase
import ru.sibfu.domain.usecase.exception.NetworkResult
import javax.inject.Inject

@HiltViewModel
class UserScreenViewModel @Inject constructor(
    private val getUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(UserScreenState())
    val state: StateFlow<UserScreenState> = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: UserScreenIntent) {
        when (intent) {
            UserScreenIntent.AboutAppClicked -> _effect.trySend(ScreenEffect.NavigateToAboutApp)
            UserScreenIntent.PrivacyPolicyClicked -> _effect.trySend(ScreenEffect.NavigateToPrivacyPolicy)
            UserScreenIntent.UsageConditionClicked -> _effect.trySend(ScreenEffect.NavigateToUsageCondition)
            UserScreenIntent.OnLogoutClick -> logoutUser()
        }
    }
    fun logoutUser(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            logoutUseCase()
            _effect.send(ScreenEffect.NavigateToLogin)
        }

    }
    fun getUser(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when (val result = getUserUseCase()) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(
                        isLoading = false,
                        error = null,
                        email = result.data.email,
                        name = result.data.name,
                    ) }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.Exception -> {
//                    Log.d("Authorization_DEBUG", result.e.toString())
                    _state.update { it.copy(isLoading = false, error = "Проверьте подключение к интернету") }
                }
            }
        }
    }
    init {
        getUser()
    }




}