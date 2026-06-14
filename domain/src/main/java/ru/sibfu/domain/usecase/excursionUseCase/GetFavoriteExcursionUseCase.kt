package ru.sibfu.domain.usecase.excursionUseCase

import ru.sibfu.domain.ExcursionShortModel
import ru.sibfu.domain.interfaces.IExcursionRepository
import ru.sibfu.domain.usecase.exception.NetworkResult
import javax.inject.Inject

class GetFavoriteExcursionUseCase @Inject constructor(
    private val repository: IExcursionRepository
) {
    suspend operator fun invoke()
    : NetworkResult<List<ExcursionShortModel>> {
        val data = repository.getFavoriteExcursions()
        return data
    }
}

