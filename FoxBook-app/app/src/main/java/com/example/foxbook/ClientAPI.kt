package com.example.foxbook

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL: String = "http://192.168.0.142:8000"
object ClientAPI {

    val apiService: APIServices by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(APIServices::class.java)
    }
}