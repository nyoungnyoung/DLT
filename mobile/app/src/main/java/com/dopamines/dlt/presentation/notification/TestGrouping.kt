package com.dopamines.dlt.presentation.notification

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

//
//data class FCMRequest(
//    val to: String,
//    val data: FCMData
//)
//
//data class FCMData(
//    val title: String,
//    val message: String
//)




interface FCMApiService {
    @Headers(
        "Authorization: key=AAAA4pfPpNU:APA91bFGhl86w9sIy5g9bl0_C8a5ovh2jk5Kwk7PZuaihKxeub3F5gG53SJYT1dZatbifUcDV5UQ3vwDMG0wthYfloeKmxTvi549prPKIaBQ33Tci55P3pHmidjwGTaQZVlvX6A_SSnF",
      )
    @POST("fcm/send")
    suspend fun sendFCMRequest(@Body request: GroupRequest): Response<ResponseBody>



}


interface newApiService {
    @Headers(
        "Authorization: Bearer ya29.AAAA4pfPpNU:APA91bFGhl86w9sIy5g9bl0_C8a5ovh2jk5Kwk7PZuaihKxeub3F5gG53SJYT1dZatbifUcDV5UQ3vwDMG0wthYfloeKmxTvi549prPKIaBQ33Tci55P3pHmidjwGTaQZVlvX6A_SSnF",
        "project_id: 973209576661")
    @POST("v1/projects/d209-dopamines/messages:send")
    suspend fun notificationRequest(@Body request: NotificationRequest): Response<FCMResponse>
}

val deviceToken =
    "cab0Nrj9Sn-f4qztzc8EDm:APA91bHY-7o8o0CW8IERh0s5xPZhDBpVK5nbGDCj1jn1Wzj4vfj1kDZ9gUSmEkgXtuUi51d8-ZVrxsB2ox3PWw16cur_tta5IPvqjYDAd5-_xGs4wn9wjDW14mHSwH1QNtGhglFWUHUN"
val title = "DLT"
val body = "왜 안보여"

// FCM 서버에 요청을 보내는 함수
//private fun sendFCMRequest( deviceToken: String, title: String, message: String) {
//    val retrofit = Retrofit.Builder()
//        .baseUrl("https://fcm.googleapis.com/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    val apiService = retrofit.create(FCMApiService::class.java)
//
//    val request = FCMRequest(
//        to = deviceToken,
//        data = FCMData(
//            title = title,
//            message = message
//        )
//    )
//
//    // IO 스레드에서 실행되는 코루틴 빌더
//    CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val response = apiService.sendFCMRequest(request)
//            // 요청이 성공적으로 처리됨
//            Log.i("FCMMMMMMMMMMMMMMM", response.toString())
//        } catch (e: Exception) {
//            // 요청이 실패한 경우
//            Log.i("FCMMMMMMMMMMMMMMM실패", e.message.toString())
//        }
//    }
//}

fun notificationRequest(newgroupName:String,deviceTokenList: List<String>) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://fcm.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService2 = retrofit.create(newApiService::class.java)

    val request = NotificationRequest(
        operation = "create",
        notification_key_name = newgroupName,
        registration_ids = deviceTokenList
    )

    // IO 스레드에서 실행되는 코루틴 빌더
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = apiService2.notificationRequest(request)
            // 요청이 성공적으로 처리됨
            Log.i("그룹만들기 성공", response.body().toString())
        } catch (e: Exception) {
            // 요청이 실패한 경우
            Log.i("FCMMMMMMMMMMMMMMM실패", e.message.toString())
        }
    }
}



data class GroupRequest(
    val to: String,
    val notification: GroupNotification
)

data class GroupNotification(
    val title: String,
    val body: String
)

data class GroupResponse(
    val success: Int,
    val failure: Int
)



interface GroupApiService {
    @Headers(
        "Authorization: key=AAAA4pfPpNU:APA91bFGhl86w9sIy5g9bl0_C8a5ovh2jk5Kwk7PZuaihKxeub3F5gG53SJYT1dZatbifUcDV5UQ3vwDMG0wthYfloeKmxTvi549prPKIaBQ33Tci55P3pHmidjwGTaQZVlvX6A_SSnF"
       )
    @POST("fcm/send")
    suspend fun sendGroupRequest(@Body request: GroupRequest): Response<GroupResponse>
}

// FCM 서버에 요청을 보내는 함수
fun sendGroupRequest( groupName: String, title: String, body: String) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://fcm.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(GroupApiService::class.java)

    val request = GroupRequest(
        to = groupName,
        notification = GroupNotification(
            title = title,
            body = body
        )
    )

    // IO 스레드에서 실행되는 코루틴 빌더
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = apiService.sendGroupRequest(request)
            // 요청이 성공적으로 처리됨
            Log.i("알림보내기", response.toString())
            Log.i("알림보내기", response.body().toString())
        } catch (e: Exception) {
            // 요청이 실패한 경우
            Log.i("FCMMMMMMMMMMMMMMM실패", e.message.toString())
        }
    }
}