package ru.sibfu.domain.usecase.authentificationUseCase

import ru.sibfu.domain.AuthResult
import ru.sibfu.domain.interfaces.IAuthRepository
import ru.sibfu.domain.usecase.exception.NetworkResult
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val repository: IAuthRepository
){
    suspend operator fun invoke(
        code: String
    ): NetworkResult<AuthResult> {
        return repository.verifyOtp(code)
    }
}
