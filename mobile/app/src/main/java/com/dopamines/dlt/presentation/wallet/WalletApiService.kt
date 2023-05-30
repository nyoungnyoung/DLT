package com.dopamines.dlt.presentation.wallet

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


interface WalletApiService {

    @GET("/wallet/details")
    suspend fun getWalletData(
        @Header("Authorization") accessToken: String,
    ):Response<WalletData>


    @POST("/wallet/charge")
    suspend fun sendChargeData(
        @Header("Authorization") accessToken: String,
        @Query("money") money: Int,
        @Query("method") method: String,
        @Query("transactionDate") transactionDate: String,
        @Query("transactionTime") transactionTime: String,
        @Query("receipt") receipt: String
    ): Response<WalletData>


    @POST("/wallet/withdraw")
    suspend fun sendWithdrawData(
        @Header("Authorization") accessToken: String,
        @Query("money") money: Int,
    ):Response<WalletData>


}