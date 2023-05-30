package com.dopamines.dlt.presentation.mypage

import android.content.Context
import android.util.Log
import com.dopamines.dlt.presentation.auth.Constants
import com.dopamines.dlt.presentation.auth.SharedPreferences
import com.dopamines.dlt.presentation.gallery.GalleryApiService
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyPageRepository(context: Context) {
    private val sharedPreferences = SharedPreferences(context)
    val accessToken = sharedPreferences.accessToken


    private val apiservice = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MyPageApiService::class.java)


    suspend fun getMyInfo() :Response<MyPageData> {
        return apiservice.getMyInfo("Bearer $accessToken")
    }


}