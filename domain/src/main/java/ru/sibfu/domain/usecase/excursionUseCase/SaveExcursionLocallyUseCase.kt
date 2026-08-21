package ru.sibfu.domain.usecase.excursionUseCase

import ru.sibfu.domain.ExcursionDetailModel
import ru.sibfu.domain.interfaces.ILocalExcursionRepository
import javax.inject.Inject

class SaveExcursionLocallyUseCase @Inject constructor(
    private val localRepository: ILocalExcursionRepository
){
    suspend operator fun invoke(excursion: ExcursionDetailModel) {
        localRepository.saveExcursion(excursion)
    }
}