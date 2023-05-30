package com.dopamines.dlt.presentation.gallery

import android.content.Context
import com.dopamines.dlt.presentation.auth.Constants
import com.dopamines.dlt.presentation.auth.SharedPreferences
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GalleryRepository(context: Context) {
    private val sharedPreferences = SharedPreferences(context)

    private val apiservice = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GalleryApiService::class.java)

    val accessToken = sharedPreferences.accessToken


    suspend fun getGalleryData(date: String) :Response<Map<String, List<GalleryData>>> {
        return apiservice.getGalleryData("Bearer $accessToken", date)
    }


    suspend fun getGalleryDetailPhoto(planId:Int) :Response<GalleryDetailPhoto> {
        return apiservice.getGalleryDetailPhoto("Bearer $accessToken",planId)
    }

    suspend fun getComments(planId: Int) :Response<Map<String, List<Comment>>> {
        return apiservice.getComments("Bearer $accessToken", planId)
    }


    suspend fun getPlanEndDetailData(planId: Int):Response<PlanEndDetailData> {
        return apiservice.getPlanEndDetail("Bearer $accessToken", planId)
    }


    suspend fun createComment(planId: Int, content: String):Response<Long> {
        return apiservice.createComment("Bearer $accessToken", planId, content)
    }

    suspend fun deleteComment(commentId:Int):Response<Unit> {
        return apiservice.deleteComment("Bearer $accessToken", commentId)
    }
    suspend fun settleTime(planId: Int): Response<PayParticipantsList> {
        return apiservice.settleTime("Bearer $accessToken", planId)
    }


}



