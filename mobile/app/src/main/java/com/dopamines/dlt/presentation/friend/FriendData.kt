package com.dopamines.dlt.presentation.friend

import com.google.gson.annotations.SerializedName


data class FriendTypeData(
    val friends: List<FriendItemData>?,
    val waitings: List<FriendItemData>?
)


data class FriendItemData(
    @SerializedName("friendId")
    val friendId: Int,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profile")
    val profile: String,
    @SerializedName("profileMessage")
    val profileMessage: String,
    @SerializedName("status")
    val status: Int,
)



data class FriendSearchResponse (
    @SerializedName("accountId")
    val accountId: Int,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profile")
    val profile: String,
    @SerializedName("profileMessage")
    val profileMessage: String,
    )