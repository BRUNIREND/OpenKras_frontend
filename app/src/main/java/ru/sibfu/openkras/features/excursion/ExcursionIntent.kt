package ru.sibfu.openkras.features.excursion

import ru.sibfu.domain.ExcursionModel

sealed class ExcursionIntent {
    data object LoadData : ExcursionIntent()
    data class SelectCategory(val category: String) : ExcursionIntent()
    data class RefreshData(val Excursions: List<ExcursionModel>) : ExcursionIntent()
    data object ChangeFilterCloseState : ExcursionIntent()
    data class QueryChange(val query: String) : ExcursionIntent()
}