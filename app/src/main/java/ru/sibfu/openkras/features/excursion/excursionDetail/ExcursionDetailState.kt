package ru.sibfu.openkras.features.excursion.excursionDetail

import ru.sibfu.domain.ExcursionDetailModel

data class ExcursionDetailState (
    val isLoading: Boolean = false,
    val error: String? = null,
    val data: ExcursionDetailModel? = null,
    val isFavorite: Boolean = false
)