package ru.sibfu.domain.usecase.excursionUseCase

import ru.sibfu.domain.ExcursionShortModel
import ru.sibfu.domain.interfaces.IExcursionRepository
import ru.sibfu.domain.usecase.exception.NetworkResult
import javax.inject.Inject

class SearchExcursionUseCase @Inject constructor(
    private val repository: IExcursionRepository
){
    suspend operator fun invoke(query: String): NetworkResult<List<ExcursionShortModel>> {
        val allData = repository.getAllExcursion()
        if (allData is NetworkResult.Success) {
            val filteredData = allData.data.filter { excursion ->
                excursion.title.contains(query, ignoreCase = true) ||
                        excursion.description.contains(query, ignoreCase = true)
            }
            return NetworkResult.Success(filteredData)
        }
        return allData
    }
}