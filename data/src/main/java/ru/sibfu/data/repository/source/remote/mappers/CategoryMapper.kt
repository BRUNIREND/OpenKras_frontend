package ru.sibfu.data.repository.source.remote.mappers

import ru.sibfu.data.repository.source.remote.DTO.CategoryDTO
import ru.sibfu.domain.CategoryModel

fun CategoryDTO.toDomain(): CategoryModel = CategoryModel(
    id = this.id,
    name = this.name
)

