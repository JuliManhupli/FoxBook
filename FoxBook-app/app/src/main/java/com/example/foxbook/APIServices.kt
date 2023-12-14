package com.example.foxbook

import com.example.foxbook.api.BooksResponse
import com.example.foxbook.api.Email
import com.example.foxbook.api.Login
import com.example.foxbook.api.PasswordResetVerify
import com.example.foxbook.api.Register
import com.example.foxbook.api.SetNewPassword
import com.example.foxbook.api.UserProfile
import com.example.foxbook.api.VerifyEmail
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface APIServices {
    @POST("register/")
    fun register(@Body data: Register) : Call<ResponseBody>

    @POST("verify-email/")
    fun verify(@Body data: VerifyEmail) : Call<ResponseBody>

    @POST("resend-verification/")
    fun resendVerification(@Body data: Email) : Call<ResponseBody>

    @POST("login/")
    fun login(@Body data: Login) : Call<ResponseBody>

    @POST("password-reset-request/")
    fun passwordResetRequest(@Body data: Email) : Call<ResponseBody>

    @POST("password-reset/verify/")
    fun passwordResetVerify(@Body data: PasswordResetVerify) : Call<ResponseBody>

    @POST("password-reset/set-password/")
    fun passwordResetSetPassword(@Body data: SetNewPassword) : Call<ResponseBody>

    @GET("books/")
    fun getBooks(
        @Query("page") page: Int,
        @QueryMap filterQuery: Map<String, String>
    ): Call<BooksResponse>


    @GET("genres/")
    fun getGenres(): Call<List<String>>

    @GET("profile/")
    fun getUserProfile(@Header("Authorization") authHeader: String): Call<UserProfile>
}
