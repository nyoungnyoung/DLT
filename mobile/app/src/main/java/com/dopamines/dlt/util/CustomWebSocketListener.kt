package com.dopamines.dlt.util

import okhttp3.WebSocket

interface CustomWebSocketListener {
    fun onMessageReceived(message: String)
}