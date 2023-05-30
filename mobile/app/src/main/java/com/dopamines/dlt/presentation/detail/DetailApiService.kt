package com.dopamines.dlt.presentation.detail

import com.dopamines.dlt.presentation.home.socketData
import com.dopamines.dlt.presentation.home.userInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @GET("/plan/detail")
    suspend fun getPlanDetail(
        @Header("Authorization") accessToken: String,
        @Query("planId") planId: Int
    ): PlanDetail


    @Multipart
    @POST("/photo/register")
    suspend fun uploadImage(
        @Header("Authorization") accessToken: String,
        @Part("planId") planId: RequestBody,
        @Part photoFile: MultipartBody.Part
    ): Response<Long>

    // 접속한 user의 id 반환하는 api
    @GET("/account/id")
    suspend fun getUserId(
        @Header("Authorization") accessToken: String,
    ) : Response<userInfo>

    // 약속의 state가 1, 2일 경우 planId에 해당하는 webSocket room 생성하는 api
    @POST("/position/create")
    suspend fun getPosition(
        @Header("Authorization") accessToken: String,
        @Query("planId") planId: String,
    ) : Response<socketData>

}
