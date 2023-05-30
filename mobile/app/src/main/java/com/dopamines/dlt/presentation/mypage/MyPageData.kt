package com.dopamines.dlt.presentation.mypage

data class MyPageData(
    val accountId: Int,
    val averageArrivalTime: Int,
    val latenessRate: Int,
    val nickname: String,
    val profile: String,
    val totalCost: Int
)
