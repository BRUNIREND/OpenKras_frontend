package ru.sibfu.domain.usecase.authentificationUseCase

import ru.sibfu.domain.AuthResult
import ru.sibfu.domain.CategoryModel
import ru.sibfu.domain.UsersModel
import ru.sibfu.domain.interfaces.IAuthRepository
import ru.sibfu.domain.interfaces.ICategoryRepository
import javax.inject.Inject

class signInUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(
        username: String,
        password: String,
    ): Result<AuthResult> {
        try {
            val data = repository.signIn(
                username = username,
                password = password,
            )
            return data
        } catch (e: Exception){
            return Result.failure(e)
        }
    }
}