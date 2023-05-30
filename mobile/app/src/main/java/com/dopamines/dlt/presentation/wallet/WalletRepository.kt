package com.dopamines.dlt.presentation.wallet

import android.content.Context
import android.util.Log
import com.dopamines.dlt.presentation.auth.Constants
import com.dopamines.dlt.presentation.auth.SharedPreferences
import com.dopamines.dlt.presentation.notification.singleFCMData

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WalletRepository(context: Context) {
    private val sharedPreferences = SharedPreferences(context)

    private val apiservice = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WalletApiService::class.java)

    val accessToken = sharedPreferences.accessToken


    suspend fun getWalletData(): Response<WalletData> {
        return apiservice.getWalletData("Bearer $accessToken")
    }

    suspend fun sendChargeData(
        money: Int,
        method: String,
        transactionDate: String,
        transactionTime: String,
        transactionReceipt: String
    ): Response<WalletData> {
        return apiservice.sendChargeData(
            "Bearer $accessToken",
            money,
            method,
            transactionDate,
            transactionTime,
            transactionReceipt
        )
    }


    suspend fun sendWithdrawData(money:Int):Response<WalletData> {
        Log.i("MMMMMMMMMM",money.toString())
        return apiservice.sendWithdrawData("Bearer $accessToken", money)
    }


}