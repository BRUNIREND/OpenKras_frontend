package ru.sibfu.data.repository

import ru.sibfu.data.repository.source.remote.api.MuseumApi
import ru.sibfu.data.repository.source.remote.mappers.toDomain
import ru.sibfu.domain.CategoryModel
import ru.sibfu.domain.interfaces.ICategoryRepository

class CategoryRepositoryImpl (
    private val api: MuseumApi
): ICategoryRepository {

    override suspend fun getAllCategory(): List<CategoryModel> {
        val responseDto = api.getAllCategories()
        return responseDto.map { it.toDomain() }
    }

}
