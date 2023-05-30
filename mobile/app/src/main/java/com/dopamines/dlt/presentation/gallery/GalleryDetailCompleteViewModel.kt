package com.dopamines.dlt.presentation.gallery

import android.util.Log
import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dopamines.dlt.presentation.detail.PlanDetail
import kotlinx.coroutines.launch
import java.lang.Exception

class GalleryDetailCompleteViewModel(private val repository: GalleryRepository) :ViewModel(){

    private val _planEndDetailData = MutableLiveData<PlanEndDetailData?>()
    val planEndDetailData: MutableLiveData<PlanEndDetailData?> = _planEndDetailData

    private val _myData=  MutableLiveData<MyDetailData>()
    val myData : LiveData<MyDetailData> = _myData



    private val _waiterData = MutableLiveData<MutableList<EndPlanParticipantData>>()
    val waiterData : MutableLiveData<MutableList<EndPlanParticipantData>> = _waiterData

    private val _laterData = MutableLiveData<MutableList<EndPlanParticipantData>>()
    val laterData : MutableLiveData<MutableList<EndPlanParticipantData>> = _laterData


    private val _particiapantList = MutableLiveData<PayParticipantsList>()
    val particiapantList: LiveData<PayParticipantsList> = _particiapantList


    fun getPlanEndDetailData(planId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getPlanEndDetailData(planId)
                if (response.isSuccessful) {
                    val planEndDetailData = response.body()
                    if (planEndDetailData != null) {
                        // lateTime이 0 또는 양수인 경우를 담을 리스트
                        val positiveLateTimeList = mutableListOf<EndPlanParticipantData>()
                        // lateTime이 음수인 경우를 담을 리스트
                        val negativeLateTimeList = mutableListOf<EndPlanParticipantData>()

                        // endPlanParticipantDto 리스트를 순회하면서 lateTime이 0 또는 양수인 경우와 음수인 경우로 나누어서 각 리스트에 추가
                        planEndDetailData?.endPlanParticipantDto?.forEach { participant ->
                            if (participant.lateTime > 0) {
                                positiveLateTimeList.add(participant)
                            } else {
                                negativeLateTimeList.add(participant)
                            }
                        }
                        _planEndDetailData.value = planEndDetailData
                        _myData.value = planEndDetailData?.myDetail
                        _waiterData.value = negativeLateTimeList
                        _laterData.value = positiveLateTimeList


                        Log.i("WAITERDATA", _waiterData.value.toString())
                        Log.i("LATERDATA", _laterData.value.toString())
                    }


                }
            } catch (e: Exception) {
                Log.e("CATCH IN GALLERY COMPLETE", e.toString())
            }
        }
    }

    fun settleTime(planId:Int) {
        viewModelScope.launch {
            try {
                val response = repository.settleTime(planId)
//                _participantList.value = response.body()
                Log.i("TRY in SETTLE", "{$response}")
                if (response.isSuccessful) {
                    _particiapantList.value = response.body()
                }
            } catch (e:Exception) {
                Log.e("CATCH in SETTLE", e.toString())
            }
        }
    }



}