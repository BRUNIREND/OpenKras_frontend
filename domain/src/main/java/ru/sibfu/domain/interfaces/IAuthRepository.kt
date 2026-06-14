package ru.sibfu.domain.interfaces

import ru.sibfu.domain.AuthResult
import ru.sibfu.domain.UserRegistrationModel
import ru.sibfu.domain.UsersModel
import ru.sibfu.domain.usecase.exception.NetworkResult

interface IAuthRepository {

    fun cacheRegistrationData(user: UserRegistrationModel)
    suspend fun signIn(email: String, password: String): NetworkResult<AuthResult>
    suspend fun signUp(email: String): NetworkResult<Unit>
    suspend fun verifyOtp(code: String): NetworkResult<AuthResult>

    suspend fun reSendOtpCode(email: String): NetworkResult<Unit>
    suspend fun logout()
    suspend fun getCurrentUser(): NetworkResult<UsersModel>
}