package ru.sibfu.domain.usecase.excursionUseCase

import ru.sibfu.domain.ExcursionModel
import ru.sibfu.domain.interfaces.IExcursionRepository
import javax.inject.Inject

class SearchExcursionUseCase @Inject constructor(
    private val repository: IExcursionRepository
){
    suspend operator fun invoke(query: String): Result<List<ExcursionModel>> {
        if (query.isBlank()) {
            return Result.success(emptyList())
        }

        return try {
            val allData = repository.getAllExcursion()

            val filteredData = allData.filter { excursion ->
                excursion.title.contains(query, ignoreCase = true) ||
                        excursion.description.contains(query, ignoreCase = true)
            }

            Result.success(filteredData)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}