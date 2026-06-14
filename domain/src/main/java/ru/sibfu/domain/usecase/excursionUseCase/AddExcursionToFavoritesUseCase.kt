package ru.sibfu.domain.usecase.excursionUseCase

import ru.sibfu.domain.interfaces.IExcursionRepository
import ru.sibfu.domain.usecase.exception.NetworkResult
import javax.inject.Inject

class AddExcursionToFavoritesUseCase @Inject constructor(
    private val repository: IExcursionRepository
) {
    suspend operator fun invoke(excursionId: Int): NetworkResult<Unit> {
        return repository.addFavoriteExcursion(excursionId)
    }
}