package com.dopamines.dlt.presentation.notification

// 주제
data class UpdateTokenRequest (
    val deviceToken: String
    )

data class NotifData(
    val body: String,
    val planId: String,
    val title: String,
    val topic: String,
    val type: String
)


data class FCMNotificationKey(
    val operation: String,
    val notification_key_name: String,
    val registration_ids: List<String>
)


data class NotificationRequest(
    val operation:String,
    val notification_key_name :String ,
    val registration_ids : List<String>
)

data class FCMResponse(
    val notification_key: String
)


//data class postGroupNotification (
//    val operation : String,
//    val notification_key: String,
//    val notication_key: String,
//    val registration_ids: List<String>
//        )

data class singleFCMData(
    val body: String,
    val planId: String,
    val targetToken: String,
    val title: String,
    val type: String,

)
