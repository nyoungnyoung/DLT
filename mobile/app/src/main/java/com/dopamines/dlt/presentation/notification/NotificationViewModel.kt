package com.dopamines.dlt.presentation.notification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NotificationViewModel(private val repository: NotificationRepository) :ViewModel() {


    private val _notifKey = MutableLiveData<String>()
    val notifKey : MutableLiveData<String> = _notifKey

    fun sendTopicPush(title:String, body:String, planId:String,  topic:String, type:String) {

        val notifData = NotifData(
            body = body,
            planId = planId,
            title = title,
            topic = topic,
            type = type,
        )
        viewModelScope.launch {
            try {
                val response = repository.sendTopicPush(notifData)
                if (response.isSuccessful) {
                    Log.i("TRY IN PUSH", response.body().toString())
                }

            } catch (e: Exception) {
                Log.e("CATCH IN PUSH", e.toString())
            }
        }
    }

    fun registerGroup(body: String, deviceTokenList: List<String>) {
        viewModelScope.launch {
            try {
                val response = repository.registerGroup(body, deviceTokenList)
                if (response.isSuccessful){
                    Log.i("TRY IN REGISTER", response.body().toString())
                }
            } catch (e:Exception){
                Log.e("CATCH IN REGISTER", e.toString())
            }
        }
    }

    fun singleFCMRequest(body:String, planId:String, targetToken:String, title:String, type:String ) {
        viewModelScope.launch {

            val response = repository.singleFCMRequest(body, planId, targetToken, title,type)
            Log.i("DOOOOOOOOOOOOO", "${response}")
            try {

                if (response.isSuccessful){
                    Log.i("TRY IN SINGLEFCM", response.body().toString())
                }
            } catch (e:Exception){
                Log.e("CATCH IN SINGLEFCM", e.toString())
            }
        }
    }

}