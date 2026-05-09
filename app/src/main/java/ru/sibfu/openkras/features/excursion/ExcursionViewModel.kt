package ru.sibfu.openkras.features.excursion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.sibfu.domain.usecase.categoryUseCase.GetAllCategoryUseCase
import ru.sibfu.domain.usecase.excursionUseCase.GetExcursionUseCase
import ru.sibfu.domain.usecase.excursionUseCase.SearchExcursionUseCase


@HiltViewModel
class ExcursionViewModel @Inject constructor(
    private val getExcursionsUseCase: GetExcursionUseCase,
    private val searchExcursionUseCase: SearchExcursionUseCase,
    private val getCategoryUseCase: GetAllCategoryUseCase,
) : ViewModel(){
    private val _state = MutableStateFlow(ExcursionState())
    val state: StateFlow<ExcursionState> = _state.asStateFlow()

    fun handleIntent(intent: ExcursionIntent) {
        when (intent) {
            is ExcursionIntent.LoadData -> loadInitialData()
            is ExcursionIntent.RefreshData -> loadInitialData()
            is ExcursionIntent.ChangeFilterCloseState -> _state.update { it.copy(isFilterOpen = !_state.value.isFilterOpen) }
            is ExcursionIntent.QueryChange -> {
                _state.update { it.copy(queryField = intent.query) }
                fetchExcursionQuery(intent.query)
            }
            is ExcursionIntent.SelectCategory -> {
                _state.update {
                    it.copy(selectedCategory = intent.category)
                }
                applyCategory()
            }
        }
    }
    private fun applyCategory(){
        val currentCategory = _state.value.selectedCategory

        if (currentCategory != null){
            val filtered = _state.value.items.filter {
                val matches = it.categoryId == currentCategory.toInt()
                matches
            }
            _state.update { it.copy(items = filtered) }
        }

    }
    private fun loadInitialData(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Загружаем категории и экскурсии параллельно
            val categoriesResult = async { getCategoryUseCase() }
            val excursionsResult = async { getExcursionsUseCase() }

            _state.update { it.copy(
                isLoading = false,
                categoryItems = categoriesResult.await().getOrDefault(emptyList()),
                items = excursionsResult.await().getOrDefault(emptyList())
            )}
        }
    }

    private fun fetchExcursionQuery(searchQuery: String){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            searchExcursionUseCase(searchQuery).onSuccess { list ->
                _state.update { it.copy(items = list, isLoading = false) }
            }.onFailure { error ->
                _state.update { it.copy(error = error.message, isLoading = false) }
            }
        }

    }

    private fun fetchExcursions(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getExcursionsUseCase().onSuccess { list ->
                _state.update { it.copy(items = list, isLoading = false) }
            }.onFailure { error ->
                _state.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }
}