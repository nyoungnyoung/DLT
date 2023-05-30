package com.dopamines.dlt.presentation.create

import android.content.Context
import android.util.Log
import com.dopamines.dlt.presentation.auth.Constants
import com.dopamines.dlt.presentation.auth.SharedPreferences
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CreateRepository(context: Context) {

    // accessToken 가져오기
    private val sharedPreferences = SharedPreferences(context)

    // createApi 객체 생성
    private val createApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CreateApiService::class.java)

    // accessToken 선언
    val accessToken = sharedPreferences.accessToken

    // 참가자 검색 api
    suspend fun searchPeople(keyword: String) : Response<ArrayList<PeopleSearchResponse>> {
        return createApi.searchPeople("Bearer $accessToken", keyword)
    }

    // 약속 생성 api
    suspend fun createPromise(title: String,
                              planDate: String,
                              planTime: String,
                              location: String,
                              address: String,
                              latitude: Double,
                              longitude: Double,
                              cost: Int,
                              participantIds: String?) : Response<Long> {
        return createApi.createPromise("Bearer $accessToken", title, planDate, planTime, location, address, latitude, longitude, cost, participantIds)
    }


//    suspend fun createPromise(title: String,
//                              planDate: String,
//                              planTime: String,
//                              location: String,
//                              cost: Int,
//                              participantIds: String?
//                              ): PlanId {
//        return CreateApi.createPromise("Bearer $accessToken",
//            title,
//            planDate,
//            planTime,
//            location,
//            cost,
//            participantIds)
//    }
}