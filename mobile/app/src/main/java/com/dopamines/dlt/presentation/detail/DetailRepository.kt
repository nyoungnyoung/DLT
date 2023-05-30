package com.dopamines.dlt.presentation.detail

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dopamines.dlt.presentation.auth.Constants
import com.dopamines.dlt.presentation.auth.SharedPreferences
import com.dopamines.dlt.presentation.home.socketData
import com.dopamines.dlt.presentation.home.userInfo
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class DetailRepository(context: Context) {
    private val sharedPreferences = SharedPreferences(context)
    private val apiservice = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)


    val accessToken = sharedPreferences.accessToken


    suspend fun getPlanDetail(planId: Int): PlanDetail {
        return apiservice.getPlanDetail("Bearer $accessToken", planId)
    }


    suspend fun uploadImage(planId: Long, uri: Uri): Response<Long> {

        val file = File(uri.path)
        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        val imagePart = MultipartBody.Part.createFormData("photoFile", file.name, requestBody)
        val planIdRequestBody = planId.toString().toRequestBody("application/json".toMediaTypeOrNull())
//        val planIdRequestBody = planId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        Log.i("IMAGE", imagePart.toString())
        Log.i("PLANID", planId.toString())
        Log.i("AT", accessToken.toString())
        return apiservice.uploadImage("Bearer $accessToken", planIdRequestBody, imagePart)
    }

    // userId 요청 api
    suspend fun getUserId(): Response<userInfo> {
        return apiservice.getUserId("Bearer $accessToken")
    }

    // 약속의 state가 1, 2일 경우 planId에 해당하는 webSocket room 생성하는 api
    suspend fun getPosition(planId: String): Response<socketData> {
        return apiservice.getPosition("Bearer $accessToken", planId)
    }

}

