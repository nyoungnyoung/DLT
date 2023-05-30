package com.dopamines.dlt.util

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class GlobalWebSocket {
    private lateinit var webSocket: WebSocket
    private lateinit var webSocketListener: WebSocketListener
    private val messageListeners: MutableList<CustomWebSocketListener> = mutableListOf()

    init {
        val request = Request.Builder()
            .url("ws://k8d209.p.ssafy.io:8080/ws/position")
            .build()

        val client = OkHttpClient()
        webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                // WebSocket이 연결되었을 때 호출됩니다.
                Log.i("전체 WebSocket", "열렸습니다.")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                // WebSocket으로부터 메시지를 받았을 때 호출됩니다.
                notifyMessageReceived(text)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                // WebSocket이 닫혔을 때 호출됩니다.
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                // WebSocket 연결 실패 또는 에러가 발생했을 때 호출됩니다.
            }
        }
        webSocket = client.newWebSocket(request, webSocketListener)
    }

    fun disconnect() {
        webSocket.cancel()
    }

    fun addMessageListener(listener: CustomWebSocketListener) {
        messageListeners.add(listener)
    }

    fun removeMessageListener(listener: CustomWebSocketListener) {
        messageListeners.remove(listener)
    }

    fun sendMessage(message:String) {
        webSocket.send(message)
    }

    private fun notifyMessageReceived(message: String) {
        for (listener in messageListeners) {
            listener.onMessageReceived(message)
        }
    }


}