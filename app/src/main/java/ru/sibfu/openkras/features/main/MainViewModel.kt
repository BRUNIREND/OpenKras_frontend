package ru.sibfu.openkras.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import ru.sibfu.data.repository.core.TokenManager
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    val startDestination = flow {
        val token = tokenManager.getAccessToken() // Синхронно или через Flow
        if (token != null) emit(MainState.Authorized) else emit(MainState.Unauthorized)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainState.Loading)
}