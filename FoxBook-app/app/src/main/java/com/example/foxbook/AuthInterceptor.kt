package com.example.foxbook

import android.util.Log
import com.example.foxbook.api.RefreshToken
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Check if the request method is annotated with NoAuth
        val noAuthAnnotation = request.header("NoAuth") != null

        Log.d("qwe", "noAuthAnnotation")
        Log.d("qwe", noAuthAnnotation.toString())
        if (noAuthAnnotation) {
            Log.d("qwe", "noAuthAnnotation != null")
            return chain.proceed(request)
        }

        Log.d("Token", "1 getAccessToken")
        Log.d("Token", getAccessToken().toString())

        var newRequest = request.newBuilder()
            .header("Authorization", "Bearer ${getAccessToken()}")
            .build()

        var response = chain.proceed(newRequest)
        Log.d("Token", "2 response")
        Log.d("Token", response.toString())


        if (response.code == 401) {
            response.close()
            // Call the refresh token API to obtain a new access token
            if (refreshAccessToken()) {
                // Create a new request with the updated access token
                Log.d("Token", "3 newAccessToken")
                Log.d("Token", getAccessToken().toString())

                newRequest = request.newBuilder()
                    .header("Authorization", "Bearer ${getAccessToken()}")
                    .build()

                Log.d("Token", "4 newAccessToken")
                Log.d("Token", newRequest.toString())

                // Retry the request with the new access token
                // Make a new request with the updated token
                response = chain.proceed(newRequest)
                Log.d("Token", "5 response 401")
                Log.d("Token", response.toString())

                return response
            } else {
                Log.d("Token", "No refresh token")
            }

        }
        Log.d("Token", "6 response")
        return response
    }

    private fun getAccessToken(): String {
        return TokenManager.getAccessToken()
    }

    private fun refreshAccessToken(): Boolean {
        val refreshToken = TokenManager.getRefreshToken()

        if (refreshToken.isNotEmpty()) {
            // Make a refresh token request
            val refreshCall = ClientAPI.apiService.refreshToken(RefreshToken(refreshToken))
            try {
                val refreshResponse = refreshCall.execute()

                if (refreshResponse.isSuccessful) {
                    // Update the access token
                    val responseBody  = refreshResponse.body()?.string() ?: ""
                    val json = JSONObject(responseBody)
                    val newAccessToken = json.getString("access_token")
                    Log.d("Token", "New Access Token: $newAccessToken")
                    TokenManager.saveTokens(newAccessToken, refreshToken)
                    return true
                } else {
                    // Handle a failed refresh response
                    Log.d("Token", "Refresh Token Request Failed: ${refreshResponse.code()}")
                }
            } catch (e: Exception) {
                // Handle network or other exceptions during refresh
                Log.e("Token", "Refresh Token Exception", e)
            }
        } else {
            Log.d("Token", "Refresh Token is Empty")
        }

        return false
    }
}
