package com.example.foxbook.api

data class SetNewPassword(val code: String, val newPassword: String, val newPassword2: String) {
}