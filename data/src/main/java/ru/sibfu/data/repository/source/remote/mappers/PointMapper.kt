package ru.sibfu.data.repository.source.remote.mappers

import ru.sibfu.data.repository.source.remote.DTO.PointDTO
import ru.sibfu.domain.PointModel

fun PointDTO.toDomain(): PointModel {
    // Выбираем русский контент или берем первый попавшийся
    val content = this.contents.find { it.lang == "ru" } ?: this.contents.firstOrNull()

    return PointModel(
        id = this.id,
        latitude = this.latitude,
        longitude = this.longitude,
        radiusMeters = this.radiusMeters,

        name = content?.name ?: "Без названия",
        address = content?.address ?: "",
        description = content?.description ?: "",
        // Вызываем маппер для медиа внутри точки
        audioUrl = content?.media?.filter { it.mediaType == "audio" }?.map {it.fileUrl} ?: emptyList(),
        images = content?.media?.filter { it.mediaType == "image" }?.map { it.fileUrl } ?: emptyList(),
        position = this.position,
    )
}