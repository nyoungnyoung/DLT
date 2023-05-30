package com.dopamines.dlt.presentation.friend

import android.content.Context
import com.dopamines.dlt.presentation.auth.Constants
import com.dopamines.dlt.presentation.auth.SharedPreferences
import retrofit2.Response

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FriendRepository(context: Context) {
    private val sharedPreferences = SharedPreferences(context)

    private val apiservice = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FriendApiService::class.java)

    val accessToken = sharedPreferences.accessToken



    suspend fun getFriendData():Response<FriendTypeData> {
        return apiservice.getFriendData("Bearer $accessToken")
    }

    suspend fun searchFriend(keyword: String):Response<ArrayList<FriendSearchResponse>> {
        return apiservice.searchFriend("Bearer $accessToken", keyword)
    }
}