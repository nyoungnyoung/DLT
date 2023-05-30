package com.dopamines.dlt.presentation.detail

import com.google.gson.annotations.SerializedName
import java.time.LocalTime


data class PlanDetail(
    @SerializedName("diffDay")
    val diffDay: Int,
    @SerializedName("cost")
    val cost: Int,
    @SerializedName("location")
    val location: String,
    @SerializedName("participantCount")
    val participantCount: Int,
    @SerializedName("participantList")
    val participantList: List<Participant>,
    @SerializedName("planDate")
    val planDate: String,
    @SerializedName("planId")
    val planId: Int,
    @SerializedName("planTime")
    val planTime: String,
    @SerializedName("state")
    val state: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude:Double,
    @SerializedName("isPhoto")
    val isPhoto:Boolean
)

data class Participant(
    @SerializedName("accountId")
    val accountId: Int,
    @SerializedName("designation")
    val designation: Int,
    @SerializedName("isArrived")
    val isArrived: Boolean,
    @SerializedName("isHost")
    val isHost: Boolean,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profile")
    val profile: String
)



