package com.example.foxbook

import com.example.foxbook.api.AddRemoveFavorite
import com.example.foxbook.api.BooksResponse
import com.example.foxbook.api.CheckIfBookInFavorites
import com.example.foxbook.api.Email
import com.example.foxbook.api.Login
import com.example.foxbook.api.PasswordResetVerify
import com.example.foxbook.api.RefreshToken
import com.example.foxbook.api.Register
import com.example.foxbook.api.SetNewPassword
import com.example.foxbook.api.UserProfile
import com.example.foxbook.api.VerifyEmail
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface APIServices {

    @Headers("NoAuth: true")
    @POST("refresh-token/")
    fun refreshToken(@Body data: RefreshToken): Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("register/")
    fun register(@Body data: Register) : Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("verify-email/")
    fun verify(@Body data: VerifyEmail) : Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("resend-verification/")
    fun resendVerification(@Body data: Email) : Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("login/")
    fun login(@Body data: Login) : Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("password-reset-request/")
    fun passwordResetRequest(@Body data: Email) : Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("password-reset/verify/")
    fun passwordResetVerify(@Body data: PasswordResetVerify) : Call<ResponseBody>

    @Headers("NoAuth: true")
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


    @GET("favorites/check/{bookId}/")
    fun checkIfBookInFavorites(@Path("bookId") bookId: Int): Call<CheckIfBookInFavorites>
    @POST("favorites/add/{book_id}/")
    fun addToFavorites(@Path("book_id") bookId: Int): Call<AddRemoveFavorite>

    @DELETE("favorites/remove/{book_id}/")
    fun removeFromFavorites(@Path("book_id") bookId: Int): Call<AddRemoveFavorite>
}
