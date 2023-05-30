package com.dopamines.dlt.presentation.notification

import com.dopamines.dlt.presentation.auth.DeviceTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface NotificationApiService {

    @PUT("/fcm/update")
    suspend fun updateTokenRequest(
        @Header("Authorization") accessToken: String,
        @Body deviceToken: UpdateTokenRequest,
    ): Response<Unit>


    // planId 가져오기
//    @GET("/fcm/planIdList")
//    suspend fun getPlanIdList(
//        @Header("Authorization") accessToken: String,
//    ): Response<List<Int>>


    @POST("/fcm/topicPush")
    suspend fun sendTopicPush(
        @Header("Authorization") accessToken: String,
        @Body notification: NotifData
    ): Response<Unit>


    // 기기그룹 생성하기
    @Headers(
        "Authorization: key=AAAA4pfPpNU:APA91bFGhl86w9sIy5g9bl0_C8a5ovh2jk5Kwk7PZuaihKxeub3F5gG53SJYT1dZatbifUcDV5UQ3vwDMG0wthYfloeKmxTvi549prPKIaBQ33Tci55P3pHmidjwGTaQZVlvX6A_SSnF",
        "project_id: 973209576661"
    )
    @POST("/fcm/notification")
    suspend fun notificationRequest(@Body request: NotificationRequest): Response<FCMResponse>


    @POST("/fcm/tokenPush")
    suspend fun singleFCMRequest(
        @Header("Authorization") accessToken: String,
        @Body request: singleFCMData
    ): Response<Unit>
}