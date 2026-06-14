package ru.sibfu.openkras.features.excursion

import ru.sibfu.domain.CategoryModel
import ru.sibfu.domain.ExcursionShortModel


sealed class ExcursionIntent {
    data object LoadData : ExcursionIntent()
    data class SelectCategory(val category: CategoryModel) : ExcursionIntent()
    data class RefreshData(val Excursions: List<ExcursionShortModel>) : ExcursionIntent()
    data object ChangeFilterCloseState : ExcursionIntent()
    data class QueryChange(val query: String) : ExcursionIntent()
    data class onNavigateToExcursionClick(val excursionId: Int) : ExcursionIntent()
    data class onAddExcursionToFavorites(val excursionId: Int) : ExcursionIntent()
    data class onRemoveExcursionFromFavorites(val excursionId: Int) : ExcursionIntent()

}


sealed class ExcursionEffect{
    data class NavigateToDetail(val excursionId: Int) : ExcursionEffect()
}