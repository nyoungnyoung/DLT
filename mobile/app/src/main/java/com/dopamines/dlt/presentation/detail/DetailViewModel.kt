package com.dopamines.dlt.presentation.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dopamines.dlt.presentation.home.socketData
import com.dopamines.dlt.presentation.home.userInfo
import kotlinx.coroutines.launch
import java.io.File

class DetailViewModel(private val repository: DetailRepository) : ViewModel() {

    private val _plan = MutableLiveData<PlanDetail>()
    val plan: LiveData<PlanDetail> = _plan

    private val _uploadResult = MutableLiveData<Long>()
    val uploadResult: LiveData<Long> = _uploadResult

    // userId, profile 저장해줄 변수`
    private val _userInfo = MutableLiveData<userInfo?>()
    val userInfo: MutableLiveData<userInfo?> = _userInfo

    // getPosition 결과 값 저장해 줄 변수
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


    private val _checkPhotoResponse = MutableLiveData<Boolean>()
    val checkPhotoResponse: LiveData<Boolean>
        get() = _checkPhotoResponse

    // 접속한 userId 요청 함수
    suspend fun getUserId() {
        viewModelScope.launch {
            try {
                // repository에 저장된 userId api 호출
                val response = repository.getUserId()
                if (response.isSuccessful) {
                    val user = response.body()
                    _userInfo.value = user
                    Log.i("Detail 유저정보 TRY", _userInfo.value.toString())
                } else {
                    Log.e("ERROR", response.message())
                }
            } catch (e: Exception) {
                Log.e("CATCH", e.toString())
            }
        }
    }

    fun loadDetailPlan(planId:Int) {
        viewModelScope.launch {
            try {
                _plan.value = repository.getPlanDetail(planId)
                Log.i("TRY in DETAIL", "${_plan.value}")
            } catch (e:Exception) {
                Log.e("CATCH in DETAIL", e.toString())
            }
        }
    }

    fun onCopyClicked(textToCopy: String, clipboardManager: ClipboardManager, context: Context) {
        val clipData = ClipData.newPlainText("label", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, "주소가 복사되었습니다", Toast.LENGTH_SHORT).show()
    }


    suspend fun uploadImage(planId: Long, uri: Uri) {

        val response = repository.uploadImage(planId, uri)
        Log.i("반응", response.toString())
        try {
            if(response.isSuccessful) {
                Log.i("성공",  response.body().toString())
                _uploadResult.value = response.body()
                _checkPhotoResponse.value = true

            }

        } catch (e: Exception) {
            // 예외 처리
            Log.i("이미지보내기" ,"실패")
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



}