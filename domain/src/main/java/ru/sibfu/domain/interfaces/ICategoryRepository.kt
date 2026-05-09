package ru.sibfu.domain.interfaces

import ru.sibfu.domain.CategoryModel

interface ICategoryRepository {
    suspend fun getAllCategory(): List<CategoryModel>
}