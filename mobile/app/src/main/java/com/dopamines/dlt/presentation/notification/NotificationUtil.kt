package com.dopamines.dlt.presentation.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager

import android.os.Build
import android.util.Log
import android.widget.RemoteViews

import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.dopamines.dlt.R
import com.dopamines.dlt.presentation.auth.LogInViewModel
import com.dopamines.dlt.presentation.home.HomeActivity
import com.dopamines.dlt.presentation.home.HomeViewModel

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage



class NotificationUtil:FirebaseMessagingService() {

    private lateinit var loginViewModel: LogInViewModel
    private var notificationId = 0

    override fun onCreate() {
        super.onCreate()
        // loginViewModel 초기화
        loginViewModel = LogInViewModel(this.application)
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("RTRT", "Refreshed token: $token")

        loginViewModel.sendRegistrationToServer(token)

    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        val title: String? = remoteMessage.notification?.title
        val message:String? = remoteMessage.notification?.body

        val planId: String? = remoteMessage.data["planId"]
        val type: String? = remoteMessage.data["type"]

        val vibrationPattern = longArrayOf(0, 1000, 500, 1000)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE) // 기본 알림 소리


        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("planId", planId)
            putExtra("type", type)

        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

//        val drawable = ContextCompat.getDrawable(this, R.drawable.cha_default_small)

//        val bitmapImg = drawable?.let { drawable ->
//            if (drawable is BitmapDrawable) {
//                drawable.bitmap
//            } else {
//                val bitmap = Bitmap.createBitmap(
//                    drawable.intrinsicWidth,
//                    drawable.intrinsicHeight,
//                    Bitmap.Config.ARGB_8888
//                )
//                val canvas = Canvas(bitmap)
//                drawable.setBounds(0, 0, canvas.width, canvas.height)
//                drawable.draw(canvas)
//                bitmap
//            }
//        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = "DLT"
            val channelNm = "Don't be late together"

            val notiChannel = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channelMessage = NotificationChannel(channel, channelNm,
                NotificationManager.IMPORTANCE_DEFAULT)
            channelMessage.description = "채널에 대한 설명."
            channelMessage.enableLights(true)
            channelMessage.enableVibration(true)
            channelMessage.vibrationPattern = longArrayOf(1000, 1000)
            notiChannel.createNotificationChannel(channelMessage)

            val customColor = ContextCompat.getColor(this, R.color.violet1)


            val notificationBuilder =
                NotificationCompat.Builder(this, channel)
                    .setSmallIcon(R.drawable.logo_png)
                    .setContentTitle(title) // 푸시알림의 제목
                    .setContentText(message) // 푸시알림의 내용
                    .setChannelId(channel)
                    .setVibrate(vibrationPattern)
                    .setAutoCancel(true) // 선택시 자동으로 삭제되도록 설정.
                    .setContentIntent(pendingIntent) // 알림을 눌렀을때 실행할 인텐트 설정.
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                    .setSound(soundUri)
                    .setColor(customColor)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .setGroup(type)


            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notificationBuilder.build())
            notificationId++ // 알림 ID 증가


            // 그룹화
            val groupSummaryBuilder = NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.logo_png)
                .setGroup(type)
                .setGroupSummary(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)


            notificationManager.notify(type.hashCode(), groupSummaryBuilder.build())

        } else {
            val notificationBuilder =
                NotificationCompat.Builder(this, "")
                    .setSmallIcon(R.drawable.logo_png)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setVibrate(vibrationPattern)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                    .setSound(soundUri)
                    .setColor(Color.RED)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .setGroup(type)


            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notificationBuilder.build())

            notificationId++ // 알림 ID 증가

            // 그룹화
            val groupSummaryBuilder = NotificationCompat.Builder(this, "")
                .setSmallIcon(R.drawable.logo_png)
                .setGroup(type)
                .setGroupSummary(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)


            notificationManager.notify(type.hashCode(), groupSummaryBuilder.build())

        }
    }

}



