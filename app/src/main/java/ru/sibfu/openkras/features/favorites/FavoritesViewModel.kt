package ru.sibfu.openkras.features.favorites

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
import ru.sibfu.domain.usecase.excursionUseCase.GetFavoriteExcursionUseCase
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteExcursionsUseCase: GetFavoriteExcursionUseCase,
    private val getCategoryUseCase: GetAllCategoryUseCase,
) : ViewModel(){
    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    private val _effect = Channel<FavoritesEffect>()
    val effect = _effect.receiveAsFlow()


    init {
        handleIntent(FavoritesIntent.LoadData)
    }
    fun handleIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.LoadData -> loadInitialData()
            is FavoritesIntent.RefreshData -> loadInitialData()
            is FavoritesIntent.QueryChange -> {
                _state.update { it.copy(queryField = intent.query) }
                applyFilters()
            }
            is FavoritesIntent.SelectCategory -> {
                _state.update {
                    it.copy(selectedCategory = intent.category)
                }
                applyFilters()
            }
            is FavoritesIntent.onNavigateToExcursionClick -> {
                _effect.trySend(FavoritesEffect.NavigateToExcursion(intent.excursionId))
            }
        }
    }
    private fun loadInitialData(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val categoriesResult = async { getCategoryUseCase() }
            when (val result = getFavoriteExcursionsUseCase()){
                is NetworkResult.Success -> {
                    _state.update { it.copy(
                        isLoading = false,
                        categoryItems = categoriesResult.await().getOrDefault(emptyList()),
                        allFavoriteItems = result.data,
                        displayedItems = result.data
                    )}
                    applyFilters()
                }

                is NetworkResult.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                is NetworkResult.Exception -> _state.update { it.copy(isLoading = false, error = result.e.message) }
            }
        }
    }
    private fun applyFilters() {
        _state.update { currentState ->
            val filteredList = currentState.allFavoriteItems.filter { excursion ->
                val matchesQuery = currentState.queryField.isEmpty() ||
                        excursion.title.contains(currentState.queryField, ignoreCase = true)

                val matchesCategory = currentState.selectedCategory == null ||
                        excursion.categoryId == currentState.selectedCategory.id

                matchesQuery && matchesCategory
            }

            currentState.copy(displayedItems = filteredList)
        }
    }
}