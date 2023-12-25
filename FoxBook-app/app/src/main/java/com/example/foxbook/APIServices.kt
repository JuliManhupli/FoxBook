package com.example.foxbook

import com.example.foxbook.api.BookPage
import com.example.foxbook.api.BookTextChunks
import com.example.foxbook.api.BookToRead
import com.example.foxbook.api.BooksInProgressResponse
import com.example.foxbook.api.BooksResponse
import com.example.foxbook.api.CheckIfBookInFavorites
import com.example.foxbook.api.Email
import com.example.foxbook.api.Login
import com.example.foxbook.api.Message
import com.example.foxbook.api.PasswordResetVerify
import com.example.foxbook.api.ReadingProgress
import com.example.foxbook.api.ReadingSettings
import com.example.foxbook.api.ReadingSettingsBg
import com.example.foxbook.api.ReadingSettingsText
import com.example.foxbook.api.Recommendations
import com.example.foxbook.api.RefreshToken
import com.example.foxbook.api.Register
import com.example.foxbook.api.SetNewPassword
import com.example.foxbook.api.UserProfile
import com.example.foxbook.api.UserRating
import com.example.foxbook.api.VerifyEmail
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
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
    fun register(@Body data: Register): Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("verify-email/")
    fun verify(@Body data: VerifyEmail): Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("resend-verification/")
    fun resendVerification(@Body data: Email): Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("login/")
    fun login(@Body data: Login): Call<ResponseBody>

    @POST("logout/")
    fun logout(): Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("password-reset-request/")
    fun passwordResetRequest(@Body data: Email): Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("password-reset/verify/")
    fun passwordResetVerify(@Body data: PasswordResetVerify): Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("password-reset/set-password/")
    fun passwordResetSetPassword(@Body data: SetNewPassword): Call<ResponseBody>

    @GET("books/")
    fun getBooks(
        @Query("page") page: Int,
        @QueryMap filterQuery: Map<String, String>
    ): Call<BooksResponse>

    @GET("genres/")
    fun getGenres(): Call<List<String>>

    @GET("books/{bookId}/text")
    fun getBookText(@Path("bookId") bookId: Int): Call<BookPage>


    @GET("books/{bookId}/text/сhunks")
    fun getBookTextChunks(@Path("bookId") bookId: Int): Call<BookTextChunks>

    @GET("profile/")
    fun getUserProfile(): Call<UserProfile>

    @GET("library/books/")
    fun getLibraryBooks(
        @Query("page") page: Int,
        @QueryMap filterQuery: Map<String, String>
    ): Call<BooksInProgressResponse>

    @POST("library/add/{book_id}/")
    fun addBookToLibrary(@Path("book_id") bookId: Int): Call<Message>

    @GET("library/continue-reading/")
    fun continueReading(): Call<BookToRead>

    @GET("library/recommend/")
    fun getRecommendations(): Call<Recommendations>

    @FormUrlEncoded
    @POST("library/update/reading-progress/{bookId}/")
    fun updateReadingProgress(
        @Path("bookId") bookId: Int,
        @Field("reading_progress") readingProgress: Int
    ): Call<Message>

    @GET("library/reading-progress/{bookId}/")
    fun getReadingProgress(@Path("bookId") bookId: Int): Call<ReadingProgress>

    @GET("library/user-rating/{bookId}/")
    fun getUserRating(@Path("bookId") bookId: Int): Call<UserRating>

    @FormUrlEncoded
    @POST("library/update/user-rating/{bookId}/")
    fun updateUserRating(
        @Path("bookId") bookId: Int,
        @Field("user_rating") userRating: Double
    ): Call<Message>

    @POST("reading-settings/add/")
    fun addReadingSettings(@Body request: ReadingSettings): Call<Message>


    @GET("reading-settings/text/")
    fun getReadingSettingsText(): Call<ReadingSettingsText>


    @GET("reading-settings/bg/")
    fun getReadingSettingsBg(): Call<ReadingSettingsBg>



    @GET("favorites/books/")
    fun getFavouriteBooks(
        @Query("page") page: Int,
        @QueryMap filterQuery: Map<String, String>
    ): Call<BooksResponse>

    @GET("favorites/check/{bookId}/")
    fun checkIfBookInFavorites(@Path("bookId") bookId: Int): Call<CheckIfBookInFavorites>

    @POST("favorites/add/{book_id}/")
    fun addToFavorites(@Path("book_id") bookId: Int): Call<Message>

    @DELETE("favorites/remove/{book_id}/")
    fun removeFromFavorites(@Path("book_id") bookId: Int): Call<Message>
}
