package com.example.foxbook

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREF_NAME = "TokenPrefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private fun getSharedPreferences(): SharedPreferences {
        return FoxBook.getAppContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        with(getSharedPreferences().edit()) {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }

    fun getAccessToken(): String {
        return getSharedPreferences().getString(KEY_ACCESS_TOKEN, "") ?: ""
    }

    fun getRefreshToken(): String {
        return getSharedPreferences().getString(KEY_REFRESH_TOKEN, "") ?: ""
    }

    fun clearTokens() {
        getSharedPreferences().edit().clear().apply()
    }
}