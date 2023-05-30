package com.dopamines.dlt.presentation.home

import android.provider.ContactsContract.CommonDataKinds.Nickname

// 약속 목록 리사이클러뷰에 사용되는 item data
data class HomeData(
    val planId: Int,
    val title: String,
    val planDate: String,
    val planTime: String,
    val location: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val diffHours: Int,
    val diffMinutes: Int,
    val participantCount: Int,
    val state: Int,
    val participantList: ArrayList<participantInfo>
)

// 참여자 프로필 사진 리스트
data class participantInfo(
    val accountId: Int,
    val profile: String
)

// 현재 접속한 유저의 Info data
data class userInfo(
    val id: Int,
    val profile: String,
    val nickname: String
)

// 장소 검색 리사이클러뷰 아이템 data class
data class MapLayout (
    val x: Double,
    val y: Double,
)

// 웹소켓 create 시 반환하는 response data
data class socketData (
    val roomId: String,
    val state: String,
        )

// 웹소켓 메시지 전송에 사용하는 data
data class socketMessage (
    val type: String? = null,
    val roomId: String? = null,
    val sender: String? = null,
    val profile: String? = null,
    val message: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val nickname: String? = null
        )