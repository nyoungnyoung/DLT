package com.dopamines.dlt.presentation.notification

import android.content.Context
import com.dopamines.dlt.presentation.auth.Constants
import com.dopamines.dlt.presentation.auth.SharedPreferences
import com.dopamines.dlt.presentation.detail.ApiService
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotificationRepository(context:Context) {

    private val sharedPreferences = SharedPreferences(context)
    private val notificationApiservice = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NotificationApiService::class.java)

    val accessToken = sharedPreferences.accessToken

    suspend fun sendTopicPush(notifData: NotifData): Response<Unit> {
        return notificationApiservice.sendTopicPush("Bearer $accessToken", notifData)
    }

    suspend fun registerGroup(newGroupName:String,deviceTokenList: List<String>):Response<FCMResponse> {

        val request = NotificationRequest(
            operation = "create",
            notification_key_name = newGroupName,
            registration_ids = deviceTokenList
        )
        return notificationApiservice.notificationRequest(request)
    }

    suspend fun singleFCMRequest(body:String, planId:String, targetToken:String, title:String, type:String ) :Response<Unit> {
        val request = singleFCMData(
            body,
            planId,
            targetToken,
            title,
            type
        )
        return notificationApiservice.singleFCMRequest("Bearer $accessToken", request)
    }
}