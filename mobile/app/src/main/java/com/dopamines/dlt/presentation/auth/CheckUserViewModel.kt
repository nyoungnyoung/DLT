package com.dopamines.dlt.presentation.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dopamines.dlt.MainActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CheckUserViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthApiService::class.java)

    // UI와 관련된 데이터를 선언
    private val _checkUserResponse = MutableLiveData<CheckUserResponse?>()
    val checkUserResponse: MutableLiveData<CheckUserResponse?>
        get() = _checkUserResponse

    private val tokenPrefs = SharedPreferences(application)



    suspend fun sendKakaoTokenToBackend(accessToken: String) {
        val response = apiService.checkUser(CheckUserRequest(accessToken))
        try {
            Log.i("가입확인" ,response.body().toString())
            if (response.isSuccessful) {
                response.body()?.let { kakaoLoginResponse ->
                    _checkUserResponse.value = kakaoLoginResponse
                    tokenPrefs.profile = kakaoLoginResponse?.profile
                }
            } else {
                // 오류 처리

            }
        } catch (e: Exception) {
            // 예외 처리
        }
    }


}