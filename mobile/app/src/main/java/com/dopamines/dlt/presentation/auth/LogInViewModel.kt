package com.dopamines.dlt.presentation.auth

import android.app.Application
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class LogInViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthApiService::class.java)

    private val tokenPrefs = SharedPreferences(application)

    private val _checkloginResponse = MutableLiveData<Boolean>()
    val checkloginResponse: LiveData<Boolean>
        get() = _checkloginResponse


    private val sharedPreferences = SharedPreferences(application)
    val accessToken = sharedPreferences.accessToken

   fun loginToDLT(email: String, kakaoId: String) {
        viewModelScope.launch {
            val response: Response<LoginResponse> = apiService.login(email, kakaoId)
            try {
                // sharedpreference로 토큰 저장
                tokenPrefs.accessToken = response.body()?.access_token
                tokenPrefs.refreshToken = response.body()?.refresh_token
                tokenPrefs.nickname = response.body()?.nickname


                _checkloginResponse.value = response?.isSuccessful
                Log.i("LoginTRY", response.body().toString())
            } catch (e: Exception) {
                Log.e("LoginCATCH", "실패")
            }
        }
    }


    fun sendRegistrationToServer(deviceToken: String) {
        val request = DeviceTokenRequest(deviceToken)
        viewModelScope.launch {
            try {

                val response:Response<Unit> = apiService.sendRegistrationToServer("Bearer $accessToken", request)
                if(response.isSuccessful) {
                    Log.i("TRY IN DEVICETOKEN", "${response.body()}")
                }
            } catch (e: Exception) {
                Log.e("CATCH IN DEVICETOKEN", e.message.toString())
            }
        }
    }


    fun updateDeviceToken(deviceToken: String) {
        val request = DeviceTokenRequest(deviceToken)
        viewModelScope.launch {
            val response:Response<Unit> = apiService.updateDeviceToken("Bearer $accessToken", request)
            Log.i("RESPONSE", response.body().toString())
            try {
                if(response.isSuccessful) {
                    Log.i("TRY UPDATE IN DEVICETOKEN", "${response.body()}")
                }

            } catch (e: Exception){
                Log.e("CATCH UPDATE IN DEVICETOKEN", e.message.toString())

            }
        }
    }
}