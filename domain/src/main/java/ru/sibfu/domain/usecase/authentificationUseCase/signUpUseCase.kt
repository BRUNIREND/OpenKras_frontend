package ru.sibfu.domain.usecase.authentificationUseCase

import ru.sibfu.domain.AuthResult
import ru.sibfu.domain.interfaces.IAuthRepository
import javax.inject.Inject

class signUpUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
    ): Result<Unit> {
        try {
            val data = repository.signUp(
                username = username,
                email = email,
                password = password,
            )
            return Result.success(Unit)
        } catch (e: Exception){
            return Result.failure(e)
        }
    }
}