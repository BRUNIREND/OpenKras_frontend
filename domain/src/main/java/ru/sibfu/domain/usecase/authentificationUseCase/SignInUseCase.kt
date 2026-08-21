package ru.sibfu.domain.usecase.authentificationUseCase

import ru.sibfu.domain.AuthResult
import ru.sibfu.domain.interfaces.IAuthRepository
import ru.sibfu.domain.usecase.exception.NetworkResult
import javax.inject.Inject


class SignInUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): NetworkResult<AuthResult> {


        val data = repository.signIn(
            email = email,
            password = password,
        )
        return data
    }
}