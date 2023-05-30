package com.dopamines.dlt.presentation.home

import android.content.Context
import com.dopamines.dlt.presentation.auth.Constants
import com.dopamines.dlt.presentation.auth.SharedPreferences
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeRepository(context: Context) {

    // accessToken 가져오기
    private val sharedPreferences = SharedPreferences(context)

    // HomeApi 객체 생성
    private val HomeApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(HomeApiService::class.java)

    // accessToken 선언
    val accessToken = sharedPreferences.accessToken

    // userId 요청 api
    suspend fun getUserId(): Response<userInfo> {
        return HomeApi.getUserId("Bearer $accessToken")
    }

    // 선택 날짜의 약속 목록 요청 api
    suspend fun getPromiseList(planDate: String) : Response<ArrayList<HomeData>> {
        return HomeApi.getPromiseList("Bearer $accessToken", planDate)
    }

    // 약속의 state가 1, 2일 경우 planId에 해당하는 webSocket room 생성하는 api
    suspend fun getPosition(planId: String): Response<socketData> {
        return HomeApi.getPosition("Bearer $accessToken", planId)
    }

//    suspend fun sendRegistrationToServer(deviceToken:String):Response<ResponseBody> {
//        return HomeApi.sendRegistrationToServer("Bearer $accessToken", deviceToken)
//    }



    suspend fun getPlanIdList():Response<List<Int>> {
        return HomeApi.getPlanIdList("Bearer $accessToken")
    }
}