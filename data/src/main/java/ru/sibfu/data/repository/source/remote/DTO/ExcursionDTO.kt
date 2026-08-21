package ru.sibfu.data.repository.source.remote.DTO

import com.google.gson.annotations.SerializedName


data class ExcursionShortDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("category_id") val categoryId: Int?,
    @SerializedName("is_favorite") val isFavorite: Boolean,
    @SerializedName("images") val images: List<ExcursionMediaDTO> = emptyList(),
    @SerializedName("duration") val duration: Int? = null,
    @SerializedName("distance") val distance: Double? = null,
    @SerializedName("is_completed") val isCompleted: Boolean,

)

data class ExcursionDetailDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("category_id") val categoryId: Int?,
    @SerializedName("is_favorite") val isFavorite: Boolean,
    @SerializedName("images") val images: List<ExcursionMediaDTO> = emptyList(),
    @SerializedName("points") val points: List<PointDTO> = emptyList(),
    @SerializedName("duration") val duration: Int? = null,
    @SerializedName("distance") val distance: Double? = null,
    @SerializedName("is_completed") val isCompleted: Boolean,
)

data class ExcursionMediaDTO(
    @SerializedName("file_url") val file_url: String,
    @SerializedName("media_type") val mediaType: String,
)