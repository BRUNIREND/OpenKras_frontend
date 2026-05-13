package ru.sibfu.data.repository

import com.google.android.gms.cloudmessaging.RegisterRequest
import retrofit2.HttpException
import ru.sibfu.data.repository.core.TokenManager
import ru.sibfu.data.repository.source.remote.DTO.RegisterRequestDto
import ru.sibfu.data.repository.source.remote.api.AuthApi
import ru.sibfu.data.repository.source.remote.api.MuseumApi
import ru.sibfu.data.repository.source.remote.mappers.toDomain
import ru.sibfu.domain.AuthResult
import ru.sibfu.domain.CategoryModel
import ru.sibfu.domain.interfaces.IAuthRepository
import ru.sibfu.domain.interfaces.ICategoryRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenManager: TokenManager
): IAuthRepository {
    override suspend fun signIn(
        username: String,
        password: String
    ): Result<AuthResult> {
        return try {
            // 1. Делаем запрос к FastAPI
            val response = api.login(username, password)

            // 2. Сохраняем токен в DataStore
            tokenManager.saveAccessToken(response.accessToken)

            // 3. Возвращаем чистую модель в Domain
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(
        username: String,
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            val request = RegisterRequestDto(username, email, password)
            api.register(request)
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(Exception("Ошибка бэкенда: ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyOtp(
        email: String,
        code: String
    ): Result<AuthResult> {
        return try {
            val response = api.verifyOtp(email, code)
            tokenManager.saveAccessToken(response.accessToken)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.deleteToken()
    }
}
