package ru.sibfu.openkras.features.excursion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.sibfu.domain.usecase.categoryUseCase.GetAllCategoryUseCase
import ru.sibfu.domain.usecase.exception.NetworkResult
import ru.sibfu.domain.usecase.excursionUseCase.AddExcursionToFavoritesUseCase
import ru.sibfu.domain.usecase.excursionUseCase.GetExcursionUseCase
import ru.sibfu.domain.usecase.excursionUseCase.RemoveExcursionFromFavoritesUseCase
import ru.sibfu.openkras.features.excursion.ExcursionEffect.NavigateToDetail
import javax.inject.Inject


@HiltViewModel
class ExcursionViewModel @Inject constructor(
    private val getExcursionsUseCase: GetExcursionUseCase,
    private val getCategoryUseCase: GetAllCategoryUseCase,
    private val addExcursionToFavoritesUseCase: AddExcursionToFavoritesUseCase,
    private val removeExcursionFromFavoritesUseCase: RemoveExcursionFromFavoritesUseCase
) : ViewModel(){
    private val _state = MutableStateFlow(ExcursionState())
    val state: StateFlow<ExcursionState> = _state.asStateFlow()


    private val _effect = Channel<ExcursionEffect>()
    val effect = _effect.receiveAsFlow()
    fun handleIntent(intent: ExcursionIntent) {
        when (intent) {
            is ExcursionIntent.LoadData -> loadInitialData()
            is ExcursionIntent.RefreshData -> loadInitialData()
            is ExcursionIntent.ChangeFilterCloseState -> _state.update { it.copy(isFilterOpen = !_state.value.isFilterOpen) }
            is ExcursionIntent.QueryChange -> {
                _state.update { it.copy(queryField = intent.query) }
                applyFilters()
            }
            is ExcursionIntent.SelectCategory -> {
                _state.update {
                    it.copy(selectedCategory = intent.category)
                }
                applyFilters()
            }
            is ExcursionIntent.onNavigateToExcursionClick -> {
                viewModelScope.launch {
                    _effect.send(NavigateToDetail(intent.excursionId))
                }
            }

            is ExcursionIntent.onAddExcursionToFavorites -> viewModelScope.launch {
                addExcursionToFavoritesUseCase(intent.excursionId)
                _state.update { currentState ->
                    currentState.copy(
                        items = currentState.items.map {
                            if (it.id == intent.excursionId) it.copy(isFavorite = true) else it
                        }
                    )
                }
            }
            is ExcursionIntent.onRemoveExcursionFromFavorites -> viewModelScope.launch {
                removeExcursionFromFavoritesUseCase(intent.excursionId)
                _state.update { currentState ->
                    currentState.copy(
                        items = currentState.items.map {
                            if (it.id == intent.excursionId) it.copy(isFavorite = false) else it
                        }
                    )
                }
            }
        }
    }
    private fun applyFilters() {
        _state.update { currentState ->
            val filteredList = currentState.allItems.filter { excursion ->
                val matchesQuery = currentState.queryField.isEmpty() ||
                        excursion.title.contains(currentState.queryField, ignoreCase = true)

                val matchesCategory = currentState.selectedCategory == null ||
                        excursion.categoryId == currentState.selectedCategory.id

                matchesQuery && matchesCategory
            }

            currentState.copy(items = filteredList)
        }
    }
    private fun loadInitialData(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val categoriesResult = async { getCategoryUseCase() }
            when (val result = getExcursionsUseCase()){
                is NetworkResult.Success -> {
                    _state.update { it.copy(
                        isLoading = false,
                        categoryItems = categoriesResult.await().getOrDefault(emptyList()),
                        allItems = result.data,
                        items = result.data
                    )}
                    applyFilters()
                }

                is NetworkResult.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                is NetworkResult.Exception -> _state.update { it.copy(isLoading = false, error = result.e.message) }
            }
        }
    }
}