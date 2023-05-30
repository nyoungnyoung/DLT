package com.dopamines.dlt.presentation.friend

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface FriendApiService {

    @GET("/friend/get")
    suspend fun getFriendData(
        @Header("Authorization") accessToken: String,
    ):Response<FriendTypeData>


    @GET("/account/search")
    suspend fun searchFriend(
        @Header("Authorization") accessToken: String,
        @Query("keyword") keyword: String
    ) : Response<ArrayList<FriendSearchResponse>>
}