package ru.sibfu.data.repository.source.remote.api

import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import ru.sibfu.data.repository.source.remote.DTO.RegisterRequestDto
import ru.sibfu.data.repository.source.remote.DTO.TokenResponseDto
import ru.sibfu.data.repository.source.remote.DTO.UserDto

interface AuthApi {
    @FormUrlEncoded
    @POST(versionApi + "auth/login")
    suspend fun login(
        @Field("username") user: String,
        @Field("password") password: String
    ): TokenResponseDto

    @POST(versionApi + "auth/register")
    suspend fun register(@Body request: RegisterRequestDto): UserDto

    @POST(versionApi + "auth/verify-otp")
    suspend fun verifyOtp(@Body email:String, code:String): TokenResponseDto
}