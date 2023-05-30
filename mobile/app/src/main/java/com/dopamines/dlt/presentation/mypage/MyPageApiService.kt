package com.dopamines.dlt.presentation.mypage

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface MyPageApiService {

    @GET("/my/info")
    suspend fun getMyInfo(
        @Header("Authorization") accessToken: String,
    ):Response<MyPageData>
}