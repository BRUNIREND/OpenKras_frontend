package ru.sibfu.data.repository.source.remote.mappers

import ru.sibfu.data.repository.source.remote.DTO.CategoryDTO
import ru.sibfu.data.repository.source.remote.DTO.ExcursionDTO
import ru.sibfu.domain.CategoryModel
import ru.sibfu.domain.ExcursionModel

fun CategoryDTO.toDomain(): CategoryModel = CategoryModel(
    id = this.id,
    name = this.name
)

