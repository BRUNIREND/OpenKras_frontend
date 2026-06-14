package ru.sibfu.openkras.features.favorites

import ru.sibfu.domain.CategoryModel
import ru.sibfu.domain.ExcursionShortModel

data class FavoritesState(
    val allItems: List<ExcursionShortModel> = emptyList(), // Хранилище для всего списка
    val items: List<ExcursionShortModel> = emptyList(),    // То, что отображается на экране прямо сейчас
    val allFavoriteItems: List<ExcursionShortModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val queryField: String = "",
    val selectedCategory: CategoryModel? = null,
    val categoryItems: List<CategoryModel> = emptyList(),
) {
    val displayedItems: List<ExcursionShortModel>
        get() {
            return allFavoriteItems.filter { excursion ->
                // Сравнение по ID категории. Если выбрана категория "0" или null — показываем всё.
                val matchesCategory = selectedCategory == null ||
                        selectedCategory.id == 0 ||
                        excursion.categoryId == selectedCategory.id

                val matchesSearch = queryField.isBlank() ||
                        excursion.title.contains(queryField, ignoreCase = true) ||
                        excursion.description.contains(queryField, ignoreCase = true)

                matchesCategory && matchesSearch
            }
        }
}