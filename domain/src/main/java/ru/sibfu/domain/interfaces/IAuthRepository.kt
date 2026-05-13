package ru.sibfu.domain.interfaces

import ru.sibfu.domain.AuthResult

interface IAuthRepository {
    suspend fun signIn(username: String, password: String): Result<AuthResult>
    suspend fun signUp(username: String, email: String, password: String): Result<Unit>
    suspend fun verifyOtp(email: String, code: String): Result<AuthResult>
    suspend fun logout()
}