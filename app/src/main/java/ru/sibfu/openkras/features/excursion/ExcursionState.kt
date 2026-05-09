package ru.sibfu.openkras.features.excursion

import ru.sibfu.domain.CategoryModel
import ru.sibfu.domain.ExcursionModel

data class ExcursionState(
    val items: List<ExcursionModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFilterOpen: Boolean = false,
    val queryField: String? = null,
    val categoryItems: List<CategoryModel> = emptyList(),
    val selectedCategory: String? = null,
)
