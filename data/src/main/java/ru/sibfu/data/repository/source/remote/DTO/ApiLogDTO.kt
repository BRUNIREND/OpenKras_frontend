package ru.sibfu.data.repository.source.remote.DTO

import com.google.gson.annotations.SerializedName

data class ApiError(
    @SerializedName("detail") val message: String
)