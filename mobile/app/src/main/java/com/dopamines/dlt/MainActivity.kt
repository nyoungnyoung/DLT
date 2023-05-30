package com.dopamines.dlt

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.dopamines.dlt.util.CustomWebSocketListener
import com.dopamines.dlt.util.GlobalWebSocket
import com.google.gson.Gson

import com.kakao.sdk.common.KakaoSdk
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getHashKey(this)
    }
}

class GlobalApplication : Application() {

//    lateinit var webSocket: WebSocket
//    var customWebSocketListener: CustomWebSocketListener? = null

    lateinit var globalWebSocket: GlobalWebSocket

    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들
        // Kakao SDK 초기화
        KakaoSdk.init(this, "65c88d08639f2c8db205578c219ba95c")

        globalWebSocket = GlobalWebSocket()

//        val request = Request.Builder()
//            .url("ws://k8d209.p.ssafy.io:8080/ws/position")
//            .build()
//
//        val listener = object: WebSocketListener() {
//            override fun onOpen(webSocket: WebSocket, response: Response) {
//                super.onOpen(webSocket, response)
//                Log.i("전체 WebSocket", "열렸습니다.")
//
//            }
//
//            override fun onMessage(webSocket: WebSocket, text: String) {
//                Log.i("MainActivity WebSocket onMessage", text)
//                customWebSocketListener?.onMessageReceived(webSocket, text)
////                // 웹소켓으로부터 문자열 메시지를 받음
////                super.onMessage(webSocket, text)
////
////                Log.i("전체 WebSocket", "Message 수신")
////                val gson = Gson()
////                val message = gson.fromJson(text, socketMessage::class.java)
////
////                // ENTER & MOVE -> 좌표 데이터로 마커
////                when (message.type) {
////                    "ENTER" -> {
////                        Log.i("전체 웹소켓에서 ENTER메시지를 받았습니다", message.toString())}
////                    "MOVE" -> {
////                        Log.i("전체 웹소켓에서 MOVE메시지를 받았습니다", message.toString())}
////                    "ARRIVE" -> {
////                        Log.i("전체 웹소켓에서 ARRIVE메시지를 받았습니다", message.toString())}
////                }
//            }
//        }
//        val client = OkHttpClient()
//        webSocket = client.newWebSocket(request, listener)


    }

}
fun getHashKey(context: Context): String? {
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
    for (signature in packageInfo.signatures) {
        val md = MessageDigest.getInstance("SHA")
        md.update(signature.toByteArray())
        val hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT)
        Log.d("Hash Key:", hashKey)
        return hashKey
    }
    return null
}
