package ru.sibfu.data.repository.source.remote.api

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.sibfu.data.repository.source.remote.DTO.CategoryDTO
import ru.sibfu.data.repository.source.remote.DTO.ExcursionDetailDTO
import ru.sibfu.data.repository.source.remote.DTO.ExcursionShortDTO
import ru.sibfu.data.repository.source.remote.DTO.UserResponseDto


const val versionApi = "/api/v1"

interface MuseumApi {
    @GET(versionApi + "/excursions/")
    suspend fun getAllExcursions(): List<ExcursionShortDTO>

    @GET("$versionApi/users/me/favorites")
    suspend fun getFavoriteExcursions(): List<ExcursionShortDTO>

    @GET("$versionApi/excursions/{excursion_id}")
    suspend fun getExcursionDetail(
        @Path("excursion_id") excursionId: Int
    ): ExcursionDetailDTO

    @GET(versionApi + "/category/")
    suspend fun getAllCategories(): List<CategoryDTO>

    // Добавить экскурсию в избранное
    @POST("$versionApi/users/me/favorites/{excursion_id}")
    suspend fun addFavorite(@Path("excursion_id") excursionId: Int)

    // Удалить экскурсию из избранного
    @DELETE("$versionApi/users/me/favorites/{excursion_id}")
    suspend fun removeFavorite(@Path("excursion_id") excursionId: Int)

    @GET(versionApi + "/users/me")
    suspend fun getCurrentUser(): UserResponseDto
}