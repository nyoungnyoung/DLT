package com.dopamines.dlt.presentation.home

import android.app.Application
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(application: Application): AndroidViewModel(application) {

    private val repository: HomeRepository = HomeRepository(application.applicationContext)

    // userId, profile 저장해줄 변수
    private val _userInfo = MutableLiveData<userInfo?>()
    val userInfo: MutableLiveData<userInfo?> = _userInfo

    private val _planDate = MutableLiveData<String>()
    val planDate: LiveData<String> = _planDate

    // getPromiseList 결과 값 저장해줄 변수 -> recyclerView의 item으로 사용될 예정!
    private val _planList = MutableLiveData<ArrayList<HomeData>?>()
    val planList: LiveData<ArrayList<HomeData>?> = _planList

    // getPosition 결과 값 저장해 줄 변수 -> 처음 API 요청 하는 사람만 state값 True
    private val _roomData = MutableLiveData<socketData?>()
    val roomData: MutableLiveData<socketData?> = _roomData

    // 유저의 현재 위치 정보 받아와서 저장해줄 변수
    private val _userX = MutableLiveData<Double?>()
    val userX: MutableLiveData<Double?> = _userX

    private val _userY = MutableLiveData<Double?>()
    val userY: MutableLiveData<Double?> = _userY

    // 웹소켓에 보내 줄 메시지 타입 변수
    private val _msgType = MutableLiveData<String>()
    val msgType: MutableLiveData<String> = _msgType

    // 접속한 userId 요청 함수
    suspend fun getUserId() {
        viewModelScope.launch {
            try {
                // repository에 저장된 userId api 호출
                val response = repository.getUserId()
                if (response.isSuccessful) {
                    val user = response.body()
                    _userInfo.value = user
                    Log.i("유저정보 TRY", _userInfo.value.toString())
                } else {
                    Log.e("ERROR", response.message())
                }
            } catch (e: Exception) {
                Log.e("CATCH", e.toString())
            }
        }
    }

    // 선택 날짜의 약속 목록 요청 함수
    suspend fun getPromiseList(planDate: String) {
        // 코루틴 시작
        viewModelScope.launch {
            try {
                // repository에 저장된 선택 날짜의 약속 목록 요청 api 호출
                val response = repository.getPromiseList(planDate)
                // 요청 성공 -> planList 업데이트해주기
                if (response.isSuccessful) {
                    val planList = response.body()
                    _planList.value = planList
                    Log.i("TRY", _planList.value.toString())
                } else {
                    // 요청 실패
                    Log.e("ERROR", response.message())
                }
            } catch (e: Exception) {
                // 에러
                Log.e("CATCH", e.toString())
            }
        }
    }

    // 약속의 state가 1, 2일 경우 planId에 해당하는 webSocket room 생성하는 api
    suspend fun getPosition(planId: String) {
        // 코루틴 시작
        viewModelScope.launch {
            try {
                // repository에 저장된 webSocket 생성 요청 api 호출
                val response = repository.getPosition(planId)
                // 요청 성공
                if (response.isSuccessful) {
                    val roomData = response.body()
                    _roomData.value = roomData
                    Log.i("웹소켓 TRY", _roomData.value.toString())
                } else {
                    // 요청 실패
                    Log.e("웹소켓 ERROR", response.toString())
                }
            } catch (e: Exception) {
                // 에러
                Log.e("웹소켓 CATCH", e.toString())
            }
        }

    }

    fun setPlanDate(planDate: String) {
        _planDate.value = planDate
    }


    private val _planIdList = MutableLiveData<List<Int>>()
    val planIdList: LiveData<List<Int>> = _planIdList

    fun getPlanIdList() {
        viewModelScope.launch {
            try {
                val response = repository.getPlanIdList()
                if (response.isSuccessful) {
                    val newList = response.body()!!
                    _planIdList.value = newList
                    Log.i("???", newList.toString())
                }
            } catch (e: Exception) {
                Log.e("CATCH IN PIL", e.toString())
            }
        }
    }

//    fun sendRegistrationToServer(deviceToken: String) {
//        Log.i("작동", "하나")
//        viewModelScope.launch {
//            val response = repository.sendRegistrationToServer(deviceToken)
//            Log.i("CHE K IN DEVICCATCHETOKEN", "${response}")
//
//            try {
//                if(response.isSuccessful) {
//                    Log.i("TRY IN DEVICETOKEN", "${response.body()}")
//                }
//            } catch (e: Exception) {
//                Log.e("CATCH IN DEVICETOKEN", e.message.toString())
//            }
//        }
//    }

}