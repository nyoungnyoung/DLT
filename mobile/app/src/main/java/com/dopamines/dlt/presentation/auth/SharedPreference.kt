package com.dopamines.dlt.presentation.auth

import android.content.Context

class SharedPreferences(context: Context) {

    private val tokenPref = context.getSharedPreferences("tokenPref", Context.MODE_PRIVATE)

    // 로그인
    var accessToken: String?
        get() = tokenPref.getString("accessToken", null)
        set(value) = tokenPref.edit().putString("accessToken", value).apply()

    var refreshToken: String?
        get() = tokenPref.getString("refreshToken", null)
        set(value) = tokenPref.edit().putString("refreshToken", value).apply()
    var nickname: String?
        get() = tokenPref.getString("nickname", null)
        set(value) = tokenPref.edit().putString("nickname", value).apply()

    // 로그아웃
    fun clearPreferences() {
        tokenPref.edit().clear().apply()
    }

    // profile image
    var profile :String?
        get() = tokenPref.getString("profile", null)
        set(value) = tokenPref.edit().putString("profile", value).apply()


}