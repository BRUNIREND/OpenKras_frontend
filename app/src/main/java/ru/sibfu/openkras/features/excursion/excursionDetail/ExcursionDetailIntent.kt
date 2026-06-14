package ru.sibfu.openkras.features.excursion.excursionDetail

sealed class ExcursionDetailIntent {
    data object DownloadLocally : ExcursionDetailIntent()
    data class StartRoute(val excursionId: Int) : ExcursionDetailIntent()
    data class onAddExcursionToFavorites(val excursionId: Int) : ExcursionDetailIntent()
    data class onRemoveExcursionFromFavorites(val excursionId: Int) : ExcursionDetailIntent()
}

sealed class ExcursionDetailEffect {
    data class NavigateToRoute(val excursionId: Int) : ExcursionDetailEffect()
}