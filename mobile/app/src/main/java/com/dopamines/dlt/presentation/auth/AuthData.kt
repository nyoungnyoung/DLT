package com.dopamines.dlt.presentation.auth



data class CheckUserRequest(
    val code: String
)

data class CheckUserResponse(
    val signup: Boolean,
    val email: String,
    val kakaoId: String,
    val nickname: String,
    val profile: String
)

data class LoginResponse(
    val access_token: String,
    val refresh_token: String,
    val nickname: String
)


