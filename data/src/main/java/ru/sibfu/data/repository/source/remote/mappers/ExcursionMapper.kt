package ru.sibfu.data.repository.source.remote.mappers

import ru.sibfu.data.repository.source.remote.DTO.ExcursionDetailDTO
import ru.sibfu.data.repository.source.remote.DTO.ExcursionShortDTO
import ru.sibfu.domain.ExcursionDetailModel
import ru.sibfu.domain.ExcursionShortModel

// Маппер для детального экрана
fun ExcursionDetailDTO.toDomain(): ExcursionDetailModel = ExcursionDetailModel(
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
    isFavorite = this.isFavorite
)

// 1. Главный маппер для всей ручки
fun ExcursionShortDTO.toDomain(): ExcursionShortModel {
    return ExcursionShortModel(
        id = this.id,
        title = this.title,
        categoryId = this.categoryId ?: 0,
        isFavorite = this.isFavorite,
        previewImageUrl = this.images.firstOrNull()?.file_url ?: "",
        description = this.description ?: "",
        distance = this.distance ?: 0.0,
        duration = this.duration ?: 0,
    )
}

