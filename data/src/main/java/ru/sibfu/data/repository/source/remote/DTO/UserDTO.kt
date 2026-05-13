package ru.sibfu.data.repository.source.remote.DTO

import com.google.gson.annotations.SerializedName


data class RegisterRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class TokenResponseDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("user") val user: UserDto
)

data class UserDto(
    val id: Int,
    val username: String,
    val email: String
)
data class OtpRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("code") val code: String,
)