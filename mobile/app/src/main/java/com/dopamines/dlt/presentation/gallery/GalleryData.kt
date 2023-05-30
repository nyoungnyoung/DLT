package com.dopamines.dlt.presentation.gallery

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable


// 달력 detail get 관련
data class GalleryData(
    val photoId: Int?,
    val planId: Int,
    val photoUrl: String?,
    val planTime: String
)


data class GalleryDetailPhoto(
    val photoId: Int,
    val photoUrl: String?,
    val planId: Int,
    val registerTime: String?
)


// 댓글 get 관련
data class CommentData(val date: String, val comments: List<Comment>)

data class Comment(
    val commentId: Int,
    val nickName: String,
    val profile: String?,
    val content: String,
    val updateTime: String
)


// 완료페이지

data class PlanEndDetailData(
    val planId: Int,
    val title: String,
    val planDate: String,
    val planTime: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val cost: Int,
    val status: Int,
    val address : String,
    val myDetail: MyDetailData,
    val endPlanParticipantDto: List<EndPlanParticipantData>,
    val isSettle: Boolean,
)

data class MyDetailData(
    val accountId: Int,
    val nickname: String,
    val profile: String?,
    val designation: Int,
    val arrivalTime: String,
    val lateTime: Int,
    val getMoney: Int?
)

data class EndPlanParticipantData(
    val accountId: Int,
    val nickname: String,
    val profile: String?,
    val lateTime: Int,
    val designation: Int,
    val paymentAvailability: Boolean,
    val deviceToken: String,
)




data class PayParticipant(
    val accountId: Int,
    val nickName: String,
    val paymentAmount: Int,
    val deviceToken :String,
)

data class PayParticipantsList(
    val success: Boolean,
    val participants: List<PayParticipant>
)

