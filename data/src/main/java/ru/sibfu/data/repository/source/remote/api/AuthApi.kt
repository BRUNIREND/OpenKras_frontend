package ru.sibfu.data.repository.source.remote.api

import retrofit2.http.Body
import retrofit2.http.POST
import ru.sibfu.data.repository.source.remote.DTO.LoginRequestDto
import ru.sibfu.data.repository.source.remote.DTO.OtpRequestDto
import ru.sibfu.data.repository.source.remote.DTO.RegisterRequestDto
import ru.sibfu.data.repository.source.remote.DTO.TokenResponseDto

interface AuthApi {

    @POST(versionApi + "/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): TokenResponseDto

    @POST(versionApi + "/auth/register/verify")
    suspend fun verifyOtp(@Body request: RegisterRequestDto): TokenResponseDto

    @POST(versionApi + "/auth/register/request")
    suspend fun requestOtp(@Body requestDto: OtpRequestDto)


}