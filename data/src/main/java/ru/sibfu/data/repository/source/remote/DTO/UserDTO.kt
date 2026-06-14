package ru.sibfu.data.repository.source.remote.DTO

import com.google.gson.annotations.SerializedName


data class RegisterRequestDto(
    @SerializedName("name") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("code") val code: String,
)
data class LoginRequestDto(
     val email: String,
     val password: String,
)

// Aka AuthResponse
data class TokenResponseDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("user") val user: UserResponseDto
)

data class UserResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String?,
    @SerializedName("email") val email: String
)
data class OtpRequestDto(
    @SerializedName("email") val email: String,
)