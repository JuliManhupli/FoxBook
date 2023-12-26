package com.example.foxbook

import com.example.foxbook.api.AccountData
import com.example.foxbook.api.BookApi
import com.example.foxbook.api.UserData
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
    fun refreshToken(@Body data: AccountData.RefreshToken): Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("register/")
    fun register(@Body data: AccountData.Register): Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("verify-email/")
    fun verify(@Body data: AccountData.VerifyEmail): Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("resend-verification/")
    fun resendVerification(@Body data: AccountData.Email): Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("login/")
    fun login(@Body data: AccountData.Login): Call<ResponseBody>

    @POST("logout/")
    fun logout(): Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("password-reset-request/")
    fun passwordResetRequest(@Body data: AccountData.Email): Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("password-reset/verify/")
    fun passwordResetVerify(@Body data: AccountData.PasswordResetVerify): Call<ResponseBody>

    @Headers("NoAuth: true")
    @POST("password-reset/set-password/")
    fun passwordResetSetPassword(@Body data: AccountData.SetNewPassword): Call<ResponseBody>

    @GET("books/")
    fun getBooks(
        @Query("page") page: Int,
        @QueryMap filterQuery: Map<String, String>
    ): Call<BookApi.BooksResponse>

    @GET("genres/")
    fun getGenres(): Call<List<String>>

    @GET("books/{bookId}/text")
    fun getBookText(@Path("bookId") bookId: Int): Call<BookApi.BookPage>

    @GET("books/{bookId}/text/сhunks")
    fun getBookTextChunks(@Path("bookId") bookId: Int): Call<BookApi.BookTextChunks>

    @GET("profile/")
    fun getUserProfile(): Call<UserData.UserProfile>

    @GET("library/books/")
    fun getLibraryBooks(
        @Query("page") page: Int,
        @QueryMap filterQuery: Map<String, String>
    ): Call<BookApi.BooksInProgressResponse>

    @POST("library/add/{book_id}/")
    fun addBookToLibrary(@Path("book_id") bookId: Int): Call<UserData.Message>

    @POST("library/remove/{book_id}/")
    fun removeBookFromLibrary(@Path("book_id") bookId: Int): Call<UserData.Message>

    @GET("library/check/{bookId}/")
    fun checkIfBookInLibrary(@Path("bookId") bookId: Int): Call<UserData.CheckIfBook>

    @GET("library/recommend/")
    fun getRecommendations(): Call<BookApi.Recommendations>

    @GET("library/continue-reading/")
    fun continueReading(): Call<BookApi.BookToRead>

    @FormUrlEncoded
    @POST("library/update/reading-progress/{bookId}/")
    fun updateReadingProgress(
        @Path("bookId") bookId: Int,
        @Field("reading_progress") readingProgress: Int
    ): Call<UserData.Message>

    @GET("library/reading-progress/{bookId}/")
    fun getReadingProgress(@Path("bookId") bookId: Int): Call<BookApi.ReadingProgress>

    @GET("library/user-rating/{bookId}/")
    fun getUserRating(@Path("bookId") bookId: Int): Call<UserData.UserRating>

    @FormUrlEncoded
    @POST("library/update/user-rating/{bookId}/")
    fun updateUserRating(
        @Path("bookId") bookId: Int,
        @Field("user_rating") userRating: Double
    ): Call<UserData.Message>

    @POST("reading-settings/add/")
    fun addReadingSettings(@Body request: UserData.ReadingSettings): Call<UserData.Message>

    @GET("reading-settings/text/")
    fun getReadingSettingsText(): Call<UserData.ReadingSettingsText>

    @GET("reading-settings/bg/")
    fun getReadingSettingsBg(): Call<UserData.ReadingSettingsBg>

    @GET("favorites/books/")
    fun getFavouriteBooks(
        @Query("page") page: Int,
        @QueryMap filterQuery: Map<String, String>
    ): Call<BookApi.BooksResponse>

    @GET("favorites/check/{bookId}/")
    fun checkIfBookInFavorites(@Path("bookId") bookId: Int): Call<UserData.CheckIfBook>

    @POST("favorites/add/{book_id}/")
    fun addToFavorites(@Path("book_id") bookId: Int): Call<UserData.Message>

    @DELETE("favorites/remove/{book_id}/")
    fun removeFromFavorites(@Path("book_id") bookId: Int): Call<UserData.Message>
}
