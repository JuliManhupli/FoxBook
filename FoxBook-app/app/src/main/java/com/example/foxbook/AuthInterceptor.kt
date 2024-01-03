package com.example.foxbook

import android.util.Log
import com.example.foxbook.api.AccountData
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Перевірка на анотацію
        val noAuthAnnotation = request.header("NoAuth") != null

        if (noAuthAnnotation) {
            return chain.proceed(request)
        }

        var newRequest = request.newBuilder()
            .header("Authorization", "Bearer ${getAccessToken()}")
            .build()

        var response = chain.proceed(newRequest)


        if (response.code == 401) {
            response.close()
            // Викликаємо оновлення токена в АПІ, щоб створити новий
            if (refreshAccessToken()) {
                // Новий запит з оновленим токеном
                newRequest = request.newBuilder()
                    .header("Authorization", "Bearer ${getAccessToken()}")
                    .build()

                // Повторити запит із новим токеном доступу
                response = chain.proceed(newRequest)

                return response
            } else {
                Log.d("SYSTEM_ERROR", "No refresh token")
            }
        }
        return response
    }

    private fun getAccessToken(): String {
        return TokenManager.getAccessToken()
    }

    private fun refreshAccessToken(): Boolean {
        val refreshToken = TokenManager.getRefreshToken()

        if (refreshToken.isNotEmpty()) {
            // Запит на оновлення токену
            val refreshCall = ClientAPI.apiService.refreshToken(
                AccountData.RefreshToken(
                    refreshToken
                )
            )
            try {
                val refreshResponse = refreshCall.execute()

                if (refreshResponse.isSuccessful) {
                    // Оновити токен доступу
                    val responseBody  = refreshResponse.body()?.string() ?: ""
                    val json = JSONObject(responseBody)
                    val newAccessToken = json.getString("access_token")
                    TokenManager.saveTokens(newAccessToken, refreshToken)
                    return true
                } else {
                    // Обробка невдалої відповіді на оновлення
                    Log.d("SYSTEM_ERROR", "Refresh Token Request Failed: ${refreshResponse.code()}")
                }
            } catch (e: Exception) {
                // Обробка помилки мережі
                Log.e("SYSTEM_ERROR", "Refresh Token Exception", e)
            }
        } else {
            Log.d("SYSTEM_ERROR", "Refresh Token is Empty")
        }
        return false
    }
}
