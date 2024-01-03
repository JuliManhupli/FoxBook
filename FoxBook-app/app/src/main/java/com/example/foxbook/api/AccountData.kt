package com.example.foxbook.api
import com.google.gson.annotations.SerializedName

class AccountData() {

    data class Email(val email: String)

    data class VerifyEmail(val otp: String)

    data class Login(val email: String, val password: String)

    data class PasswordResetVerify (val email: String, val code: String)

    data class RefreshToken(@SerializedName("refresh_token") val refreshToken: String)

    data class SetNewPassword(val code: String, val new_password: String, val new_password2: String)

    data class Register(val email: String, val name: String, val password: String, val password2: String)

}
