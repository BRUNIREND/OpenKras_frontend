package ru.sibfu.data.repository.source.remote.mappers

import ru.sibfu.data.repository.source.remote.DTO.TokenResponseDto
import ru.sibfu.data.repository.source.remote.DTO.UserResponseDto
import ru.sibfu.domain.AuthResult
import ru.sibfu.domain.UsersModel

fun UserResponseDto.toDomain(): UsersModel {
    return UsersModel(
        id = this.id,
        name = this.name,
        email = this.email
    )
}

fun TokenResponseDto.toDomain(): AuthResult {
    return AuthResult(
        token = this.accessToken,
        user = this.user.toDomain()
    )
}