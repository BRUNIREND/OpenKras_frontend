package ru.sibfu.openkras.features.excursion

import ru.sibfu.domain.CategoryModel
import ru.sibfu.domain.ExcursionShortModel

data class ExcursionState(
    val isLoading: Boolean = false,
    val allItems: List<ExcursionShortModel> = emptyList(), // Хранилище для всего списка
    val items: List<ExcursionShortModel> = emptyList(),    // То, что отображается на экране прямо сейчас
    val categoryItems: List<CategoryModel> = emptyList(),
    val selectedCategory: CategoryModel? = null,
    val queryField: String = "",
    val isFilterOpen: Boolean = false,
    val error: String? = null
)
