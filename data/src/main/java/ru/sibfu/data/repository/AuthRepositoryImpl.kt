package ru.sibfu.data.repository

import retrofit2.HttpException
import ru.sibfu.data.repository.core.TokenManager
import ru.sibfu.data.repository.core.parseErrorMessage
import ru.sibfu.data.repository.source.remote.DTO.LoginRequestDto
import ru.sibfu.data.repository.source.remote.DTO.OtpRequestDto
import ru.sibfu.data.repository.source.remote.DTO.RegisterRequestDto
import ru.sibfu.data.repository.source.remote.api.AuthApi
import ru.sibfu.data.repository.source.remote.api.MuseumApi
import ru.sibfu.data.repository.source.remote.mappers.toDomain
import ru.sibfu.domain.AuthResult
import ru.sibfu.domain.UserRegistrationModel
import ru.sibfu.domain.UsersModel
import ru.sibfu.domain.interfaces.IAuthRepository
import ru.sibfu.domain.usecase.exception.NetworkResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val museumApi: MuseumApi,
    private val tokenManager: TokenManager
): IAuthRepository {

    private var cachedUser: UserRegistrationModel? = null

    override fun cacheRegistrationData(user: UserRegistrationModel) {
        this.cachedUser = user
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): NetworkResult<AuthResult> {
        return try {
            // 1. Делаем запрос к FastAPI
            val response = api.login(LoginRequestDto(email, password))

            // 2. Сохраняем токен в DataStore
            tokenManager.saveAccessToken(response.accessToken)
            // 3. Возвращаем чистую модель в Domain
            NetworkResult.Success(response.toDomain())
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody) ?: "Произошла ошибка сервера"
            NetworkResult.Error(message = errorMessage, code = e.code())
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }


    // First step in registration
    override suspend fun signUp(
        email: String,
    ): NetworkResult<Unit> {
        return try {
            api.requestOtp(OtpRequestDto(email))
            NetworkResult.Success(Unit)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody) ?: "Произошла ошибка сервера"
            NetworkResult.Error(message = errorMessage, code = e.code())
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun verifyOtp(
        code: String,
    ): NetworkResult<AuthResult> {
        val user = cachedUser ?: return NetworkResult.Error("No cached user data")
        return try {
            val request = RegisterRequestDto(
                fullName = user.name,
                email = user.email,
                password = user.password,
                code = code
            )

            val response = api.verifyOtp(request)
            tokenManager.saveAccessToken(response.accessToken)
            NetworkResult.Success(response.toDomain())
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody) ?: "Произошла ошибка сервера"
            NetworkResult.Error(message = errorMessage, code = e.code())
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun reSendOtpCode(email: String): NetworkResult<Unit> {
        return try {
            api.requestOtp(OtpRequestDto(email))
            NetworkResult.Success(Unit)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody) ?: "Произошла ошибка сервера"
            NetworkResult.Error(message = errorMessage, code = e.code())
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun logout() {
        tokenManager.deleteToken()
    }

    override suspend fun getCurrentUser(): NetworkResult<UsersModel> {
        return try {
            val result = museumApi.getCurrentUser().toDomain()
            NetworkResult.Success(result)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody) ?: "Произошла ошибка сервера"
            NetworkResult.Error(message = errorMessage, code = e.code())
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

}
