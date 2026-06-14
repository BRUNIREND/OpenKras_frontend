package ru.sibfu.domain.usecase.authentificationUseCase

import ru.sibfu.domain.UsersModel
import ru.sibfu.domain.interfaces.IAuthRepository
import ru.sibfu.domain.usecase.exception.NetworkResult
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(
    ): NetworkResult<UsersModel> {
        try {
            val data = repository.getCurrentUser()
            return data
        } catch (e: Exception){
            return NetworkResult.Error("Пользователь не существует")
        }
    }
}

