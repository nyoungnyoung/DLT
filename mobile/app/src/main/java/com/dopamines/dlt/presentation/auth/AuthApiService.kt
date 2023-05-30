package com.dopamines.dlt.presentation.auth

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface AuthApiService {

    @POST("/account/oauth")
    suspend fun checkUser(@Body code: CheckUserRequest): Response<CheckUserResponse>
    // suspend fun login(@Query("code") code: String): Response<KakaoLoginResponse>

    @POST("/account/signup")
    suspend fun signup(
        @Query("email") email: String,
        @Query("kakaoId") kakaoId: String,
        @Query("nickname") nickname: String,
        @Query("profile") profile: String,
    ): Response<Long>


    @POST("/account/login")
    suspend fun login(
        @Query("email") email: String,
        @Query("kakaoId") kakaoId: String,
    ): Response<LoginResponse>


    @POST("/fcm/register")
    suspend fun sendRegistrationToServer(
        @Header("Authorization") accessToken: String,
        @Body deviceToken: DeviceTokenRequest,
    ): Response<Unit>


    @PUT("/fcm/update")
    suspend fun updateDeviceToken(
        @Header("Authorization") accessToken: String,
        @Body deviceToken: DeviceTokenRequest,
    ): Response<Unit>
}

data class DeviceTokenRequest(
    val deviceToken: String
)

