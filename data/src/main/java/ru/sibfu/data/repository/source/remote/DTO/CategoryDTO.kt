package ru.sibfu.data.repository.source.remote.DTO

import com.google.gson.annotations.SerializedName

data class CategoryDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
