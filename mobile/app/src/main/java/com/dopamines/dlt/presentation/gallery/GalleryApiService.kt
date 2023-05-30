package com.dopamines.dlt.presentation.gallery

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

import retrofit2.http.Query
import java.util.concurrent.locks.ReentrantLock


interface GalleryApiService {

    @GET("/photo/listMap")
    suspend fun getGalleryData(
        @Header("Authorization") accessToken: String,
        @Query("date") date: String
    ): Response<Map<String, List<GalleryData>>>


    @GET("/photo/detail")
    suspend fun getGalleryDetailPhoto(
        @Header("Authorization") accessToken: String,
        @Query("planId") planId: Int,
    ) :Response<GalleryDetailPhoto>


    @GET("/comment/list")
    suspend fun getComments(
        @Header("Authorization") accessToken: String,
        @Query("planId") planId: Int,
    ):Response<Map<String, List<Comment>>>


    @GET("/plan/endDetail")
    suspend fun getPlanEndDetail(
        @Header("Authorization") accessToken: String,
        @Query("planId") planId: Int,
    ):Response<PlanEndDetailData>



    @POST("/comment/register")
    suspend fun createComment (
        @Header("Authorization") accessToken: String,
        @Query("planId") planId: Int,
        @Query("content") content: String,
    ):Response<Long>


    @DELETE("/comment/delete")
    suspend fun deleteComment(
        @Header("Authorization") accessToken: String,
        @Query("commentId") commentId: Int,
    ):Response<Unit>



    @POST("/plan/settle")
    suspend fun settleTime(
        @Header("Authorization") accessToken: String,
        @Query("planId") planId: Int
    ):Response<PayParticipantsList>
}