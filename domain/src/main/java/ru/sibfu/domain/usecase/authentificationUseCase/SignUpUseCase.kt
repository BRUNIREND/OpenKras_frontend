package ru.sibfu.domain.usecase.authentificationUseCase

import ru.sibfu.domain.UserRegistrationModel
import ru.sibfu.domain.interfaces.IAuthRepository
import ru.sibfu.domain.usecase.exception.NetworkResult
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
    ): NetworkResult<Unit> {
        val data = repository.signUp(
            email = email
        )

        return if (data is NetworkResult.Success){
            val user = UserRegistrationModel(
                email = email,
                name = name,
                password = password
            )
            repository.cacheRegistrationData(user)
            data
        } else{
            data
        }
    }
}