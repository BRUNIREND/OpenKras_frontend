package ru.sibfu.data.repository.source.remote.DTO

import com.google.gson.annotations.SerializedName

data class PointDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("radius_meters") val radiusMeters: Int,
    @SerializedName("contents") val contents: List<PointContentDTO> = emptyList(),
    @SerializedName("order") val position: Int
)

data class PointContentDTO(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("lang") val lang: String,
    @SerializedName("address") val address: String?,
    @SerializedName("media") val media: List<PointMediaDto> = emptyList()
)

data class PointMediaDto(
    @SerializedName("file_url") val fileUrl: String,
    @SerializedName("media_type") val mediaType: String
)
