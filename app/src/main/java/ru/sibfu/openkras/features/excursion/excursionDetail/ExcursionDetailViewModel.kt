package ru.sibfu.openkras.features.excursion.excursionDetail

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
import ru.sibfu.domain.interfaces.IExcursionRepository
import ru.sibfu.domain.usecase.exception.NetworkResult
import ru.sibfu.domain.usecase.excursionUseCase.AddExcursionToFavoritesUseCase
import ru.sibfu.domain.usecase.excursionUseCase.RemoveExcursionFromFavoritesUseCase
import javax.inject.Inject


@HiltViewModel
class ExcursionDetailViewModel @Inject constructor(
    private val repository: IExcursionRepository,
    private val addExcursionToFavoritesUseCase: AddExcursionToFavoritesUseCase,
    private val removeExcursionFromFavoritesUseCase: RemoveExcursionFromFavoritesUseCase
    // SavedStateHandle можно использовать для получения id экскурсии из навигации
) : ViewModel() {

    private val _state = MutableStateFlow(ExcursionDetailState())
    val state: StateFlow<ExcursionDetailState> = _state.asStateFlow()

    private val _effect = Channel<ExcursionDetailEffect>()
    val effect = _effect.receiveAsFlow()

    fun loadExcursionDetails(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = repository.getExcursionById(id)) {
                is NetworkResult.Success -> {
                    // В реальной жизни статус избранного можно проверить локально или получить с бэка
                    _state.update { it.copy(
                        isLoading = false,
                        data = result.data,
                        isFavorite = result.data.isFavorite
                    ) }
                }
                is NetworkResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is NetworkResult.Exception -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.e.message
                        )
                    }
                }
            }
        }
    }

    fun handleIntent(intent: ExcursionDetailIntent) {
        when (intent) {
            ExcursionDetailIntent.DownloadLocally -> {
                // Логика сохранения в кэш/локальную БД
            }

            is ExcursionDetailIntent.onAddExcursionToFavorites -> viewModelScope.launch {
                addExcursionToFavoritesUseCase(intent.excursionId)
                _state.update { currentState ->
                    currentState.copy(
                        data = currentState.data?.copy(isFavorite = true),
                        isFavorite = true
                    )
                }
            }
            is ExcursionDetailIntent.onRemoveExcursionFromFavorites -> viewModelScope.launch {
                removeExcursionFromFavoritesUseCase(intent.excursionId)
                _state.update { currentState ->
                    currentState.copy(
                        data = currentState.data?.copy(isFavorite = false),
                        isFavorite = false
                    )
                }
            }

            is ExcursionDetailIntent.StartRoute -> viewModelScope.launch {
                _state.value.data?.let {
                    _effect.trySend(ExcursionDetailEffect.NavigateToRoute(it.id))
                }
            }
        }
    }
}
