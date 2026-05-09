package ru.sibfu.data.repository.source.remote.mappers

import ru.sibfu.data.repository.source.remote.DTO.ExcursionDTO
import ru.sibfu.data.repository.source.remote.DTO.PointDTO
import ru.sibfu.domain.ExcursionModel
import ru.sibfu.domain.PointModel

// 1. Главный маппер для всей ручки
fun ExcursionDTO.toDomain(): ExcursionModel = ExcursionModel(
    id = this.id,
    title = this.title,
    description = this.description ?: "",
    // Вызываем маппер для списка точек
    points = this.points.map { it.toDomain() },
    // Вызываем маппер для медиа
    coverUrl = this.images.firstOrNull()?.file_url,
    categoryId = this.categoryId,
    images = this.images.map { it.file_url },
    distance = this.distance ?: 0.0,
    duration = this.duration ?: 0,
)

