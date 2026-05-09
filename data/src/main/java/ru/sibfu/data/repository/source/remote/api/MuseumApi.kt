package ru.sibfu.data.repository.source.remote.api

import retrofit2.http.GET
import ru.sibfu.data.repository.source.remote.DTO.CategoryDTO
import ru.sibfu.data.repository.source.remote.DTO.ExcursionDTO
import ru.sibfu.domain.ExcursionModel


const val versionApi = "/api/v1"

interface MuseumApi {
    @GET(versionApi + "/excursions")
    suspend fun getAllExcursionsWithPointsContent(): List<ExcursionDTO>

    @GET(versionApi + "/category")
    suspend fun getAllCategories(): List<CategoryDTO>
}