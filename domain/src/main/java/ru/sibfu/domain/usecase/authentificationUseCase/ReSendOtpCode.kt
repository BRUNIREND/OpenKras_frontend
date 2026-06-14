package ru.sibfu.domain.usecase.authentificationUseCase

import ru.sibfu.domain.interfaces.IAuthRepository
import ru.sibfu.domain.usecase.exception.NetworkResult
import javax.inject.Inject


class ReSendOtpCodeUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(
        email: String,
    ): NetworkResult<Unit> {
        val data = repository.reSendOtpCode(
            email = email
        )
        return data
    }
}