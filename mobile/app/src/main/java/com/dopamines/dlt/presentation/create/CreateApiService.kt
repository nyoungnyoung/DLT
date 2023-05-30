package com.dopamines.dlt.presentation.create

import com.dopamines.dlt.presentation.auth.CheckUserRequest
import com.dopamines.dlt.presentation.auth.CheckUserResponse
import com.dopamines.dlt.presentation.auth.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface CreateApiService {

    // 약속 생성 API
    @POST("/plan/create")
    suspend fun createPromise(
        @Header("Authorization") accessToken: String,
        @Query("title") title: String,
        @Query("planDate") planDate: String,
        @Query("planTime") planTime : String,
        @Query("location") location : String,
        @Query("address") address : String,
        @Query("latitude") latitude : Double,
        @Query("longitude") longitude : Double,
        @Query("cost") cost  : Int,
        @Query("participantIds") participantIds : String?
    ) : Response<Long>

    // 참가자 검색 API
    @GET("/account/search")
    suspend fun searchPeople (
        @Header("Authorization") accessToken: String,
        @Query("keyword") keyword: String
    ) : Response<ArrayList<PeopleSearchResponse>>
}