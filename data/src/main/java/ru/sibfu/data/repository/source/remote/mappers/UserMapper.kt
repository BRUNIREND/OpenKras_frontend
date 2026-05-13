package ru.sibfu.data.repository.source.remote.mappers

import ru.sibfu.data.repository.source.remote.DTO.TokenResponseDto
import ru.sibfu.data.repository.source.remote.DTO.UserDto
import ru.sibfu.domain.AuthResult
import ru.sibfu.domain.UsersModel

fun UserDto.toDomain(): UsersModel {
    return UsersModel(
        id = this.id,
        username = this.username,
        email = this.email
    )
}

fun TokenResponseDto.toDomain(): AuthResult {
    return AuthResult(
        token = this.accessToken,
        user = this.user.toDomain()
    )
}