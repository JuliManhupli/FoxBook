package com.example.foxbook.api

data class SetNewPassword(val code: String, val new_password: String, val new_password2: String) {
}