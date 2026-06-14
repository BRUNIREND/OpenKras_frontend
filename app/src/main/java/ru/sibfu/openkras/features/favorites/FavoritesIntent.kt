package ru.sibfu.openkras.features.favorites

import ru.sibfu.domain.CategoryModel
import ru.sibfu.domain.ExcursionShortModel


sealed class FavoritesIntent {
    data object LoadData : FavoritesIntent()
    data class SelectCategory(val category: CategoryModel) : FavoritesIntent()
    data class RefreshData(val Excursions: List<ExcursionShortModel>) : FavoritesIntent()
    data class QueryChange(val query: String) : FavoritesIntent()
    data class onNavigateToExcursionClick(val excursionId: Int) : FavoritesIntent()
}

sealed class FavoritesEffect{
    data class ShowSnackbar(val message: String) : FavoritesEffect()
    data class NavigateToExcursion(val excursionId: Int) : FavoritesEffect()
}