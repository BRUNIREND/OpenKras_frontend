package ru.sibfu.domain.usecase.authentificationUseCase

import ru.sibfu.domain.interfaces.IAuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(
    ): Result<String> {
        try {
            repository.logout()
            return Result.success("Выход прошел успешно")
        } catch (e: Exception){
            return Result.failure(e)
        }
    }
}