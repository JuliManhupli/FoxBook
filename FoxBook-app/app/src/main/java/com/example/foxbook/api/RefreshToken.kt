package com.example.foxbook.api

import com.google.gson.annotations.SerializedName

data class RefreshToken(@SerializedName("refresh_token") val refreshToken: String){}