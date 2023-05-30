package com.dopamines.dlt.presentation.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class SignUpViewModel(application: Application):AndroidViewModel(application){

    private val apiService = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthApiService::class.java)

    private val _signUpResponse = MutableLiveData<Boolean>()
    val signUpResponse: MutableLiveData<Boolean>
        get() = _signUpResponse

    private val sharedPreferences = SharedPreferences(application)
    val profile = sharedPreferences.profile.toString()

    suspend fun signupToDLT(email: String, kakaoId: String, nickname: String, ) {
//        val response : Response<Long> = apiService.signup(email, kakaoId, nickname)
        Log.i("check", profile)
        val response = apiService.signup(email, kakaoId, nickname, profile)
        Log.d("API RESPONSE", "HTTP status code: ${response.code()}")
        Log.d("API RESPONSE", "Response body: ${response.body()}")

        viewModelScope.launch {
            try{
                Log.i("TRY!!!!!!!!!!!!!!!!!", "${response.body()}")
                if(response.isSuccessful) {
                    _signUpResponse.value = response.isSuccessful
                }
            }
            catch (e:Exception) {
                Log.e("API RESPONSE", "Error: ${e.message}, 이미 회원가입??")

            }
        }
    }
}