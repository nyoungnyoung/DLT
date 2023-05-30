package com.dopamines.dlt.presentation.home

import android.net.http.HttpResponseCache
import com.dopamines.dlt.presentation.notification.NotifData
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface HomeApiService {

    // 접속한 user의 id 반환하는 api
    @GET("/account/id")
    suspend fun getUserId(
        @Header("Authorization") accessToken: String,
    ) : Response<userInfo>

    // 선택 날짜의 약속 목록 api
    @GET("/plan/list")
    suspend fun getPromiseList(
        @Header("Authorization") accessToken: String,
        @Query("planDate") planDate: String,
    ) : Response<ArrayList<HomeData>>


    // 약속의 state가 1, 2일 경우 planId에 해당하는 webSocket room 생성하는 api
    @POST("/position/create")
    suspend fun getPosition(
        @Header("Authorization") accessToken: String,
        @Query("planId") planId: String,
    ) : Response<socketData>


    // planId 가져오기
    @GET("/fcm/planIdList")
    suspend fun getPlanIdList(
        @Header("Authorization") accessToken: String,
    ): Response<List<Int>>





}