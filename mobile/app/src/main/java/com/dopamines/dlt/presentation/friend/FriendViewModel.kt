package com.dopamines.dlt.presentation.friend

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.launch
import java.lang.Exception

class FriendViewModel(private val repository: FriendRepository):ViewModel() {


    private val _friendData = MutableLiveData<FriendTypeData?>()
    val friendData: MutableLiveData<FriendTypeData?> = _friendData

    fun getFriendData(){
        viewModelScope.launch {
            try {
                val response = repository.getFriendData()
                Log.i("RESPONSE IN FRIEND", "${response.body()}")
                if(response.isSuccessful){
                    val responseData = response.body()
                    _friendData.value = responseData
                }
            } catch (e:Exception) {
                Log.i("RESPONSE IN FRIEND", e.toString())
            }
        }
    }


//    fun searchFriend(ke){
//        viewModelScope.launch {
//            val response = repository.searchFriend()
//            try {
//                if(response.isSuccessful) {
////                    Log.i("RESPONSE IN FRIEND", )
//                }
////                Log.i("RESPONSE IN FRIEND", e.toString())
//
//            } catch (e:Exception) {
//                Log.i("RESPONSE IN FRIEND", e.toString())
//            }
//        }
//    }
}