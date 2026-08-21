package ru.sibfu.domain.interfaces

import ru.sibfu.domain.ExcursionDetailModel

interface ILocalExcursionRepository {
    suspend fun saveExcursion(excursion: ExcursionDetailModel)
    suspend fun isExcursionSaved(id: Int): Boolean
    suspend fun deleteExcursion(id: Int)
}
