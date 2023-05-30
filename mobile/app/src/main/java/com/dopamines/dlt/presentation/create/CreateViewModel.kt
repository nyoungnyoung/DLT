package com.dopamines.dlt.presentation.create

import android.app.Application
import android.text.Editable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class CreateViewModel(application: Application): AndroidViewModel(application) {

    // 참가자 검색 함수
    suspend fun searchPeople(keyword: String) {
        // 코루틴 시작
        viewModelScope.launch {
            try {
                // repository에 저장된 참가자 검색 api 호출
                val response = repository.searchPeople(keyword)
                // 요청 성공
                if (response.isSuccessful) {
                    val searchPeopleData = response.body()
                    _searchPeople.value = searchPeopleData
                    Log.i("TRY", _searchPeople.value.toString())
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

    // 약속 생성 함수
    suspend fun createPromise(
        title: String,
        planDate: String,
        planTime: String,
        location: String,
        address: String,
        latitude: Double,
        longitude: Double,
        cost: Int,
        participantIds: String?) {
        viewModelScope.launch {
            try {
                // repository에 저장된 약속 생성 api 호출
                val response = repository.createPromise(title, planDate, planTime, location, address, latitude, longitude, cost, participantIds)
                // 요청 성공
                if (response.isSuccessful) {
                    val PlanId = response.body()
                    Log.i("TRY", "${PlanId}번째 약속이 생성되었습니다.")
                } else {
                    // 요청 실패
                    Log.e("ERROR", response.code().toString())
                }
            } catch (e: Exception) {
                // 에러
                Log.e("CATCH", e.toString())
            }
        }
    }

    private val repository: CreateRepository = CreateRepository(application.applicationContext)

    private val _promiseCreate = MutableLiveData<PromiseCreate>()
    val promiseCreate: LiveData<PromiseCreate> = _promiseCreate

    // 약속생성 API 요청 위한 변수
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _planDate = MutableLiveData<String>()
    val planDate: LiveData<String> = _planDate

    private val _planTime = MutableLiveData<String>()
    val planTime: LiveData<String> = _planTime

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> = _location

    private val _address  = MutableLiveData<String>()
    val address : LiveData<String> = _address

    private val _latitude = MutableLiveData<Double?>()
    val latitude: LiveData<Double?> = _latitude

    private val _longitude = MutableLiveData<Double?>()
    val longitude: LiveData<Double?> = _longitude

    private val _cost = MutableLiveData<Int?>()
    val cost: LiveData<Int?> = _cost

    private val _participantIds = MutableLiveData<String?>()
    val participantIds: LiveData<String?> = _participantIds

    // editText 메시지 유지를 위한 변수
    private val _dateText = MutableLiveData<Editable?>()
    val dateText: LiveData<Editable?> = _dateText

    // 일정 입력 후 생성 페이지로 돌아갔다가 왔을 때 값 유지하기 위한 변수


    // searchPeople 결과 값 저장해줄 변수 -> recyclerView의 item으로 사용될 예정!
    private val _searchPeople = MutableLiveData<ArrayList<PeopleSearchResponse>?>()
    val searchPeople: LiveData<ArrayList<PeopleSearchResponse>?> = _searchPeople

    // participantInfo 저장해줄 변수 -> recyclerView의 item으로 사용!
    private val _participantInfo = MutableLiveData<ArrayList<PeopleSearchResponse>?>()
    val participantInfo: LiveData<ArrayList<PeopleSearchResponse>?> = _participantInfo

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setPlanDate(planDate: String) {
        _planDate.value = planDate
    }

    fun setPlanTime(planTime: String) {
        _planTime.value = planTime
    }

    fun setLocation(location: String) {
        _location.value = location
    }

    fun setAddress(address: String) {
        _address.value = address
    }

    fun setLatitude(latitude: Double?) {
        _latitude.value = latitude
    }

    fun setLongitude(longitude: Double?) {
        _longitude.value = longitude
    }

    fun setCost(cost: Int?) {
        _cost.value = cost
    }

    fun setParticipantIds(participantIds: String) {
        _participantIds.value = participantIds
    }

    fun setDateText(newText: Editable?) {
        _dateText.value = newText
    }

    fun setParticipantInfo(participantInfo: ArrayList<PeopleSearchResponse>?) {
        _participantInfo.value = participantInfo
    }









}