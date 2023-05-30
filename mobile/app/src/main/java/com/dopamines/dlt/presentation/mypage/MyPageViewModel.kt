package com.dopamines.dlt.presentation.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dopamines.dlt.presentation.friend.FriendTypeData
import kotlinx.coroutines.launch

class MyPageViewModel(private val repository: MyPageRepository): ViewModel() {

    private val _myInfoData = MutableLiveData<MyPageData>()
    val myInfoData: LiveData<MyPageData> = _myInfoData

    fun getMyInfo() {
        Log.i("작동", "밖")
        viewModelScope.launch {
            try {
                Log.i("try", "안")
                val response = repository.getMyInfo()
                Log.i("TRYYY", response.toString())
                if (response.isSuccessful) {

                    _myInfoData.value = response.body()
                }
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "Error: ${e.message}", e)
            }
        }
    }
}
