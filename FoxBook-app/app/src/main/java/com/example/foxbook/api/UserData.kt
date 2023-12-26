package com.example.foxbook.api

class UserData() {

    class Message (val message: String)

    class UserRating(val user_rating: Float)

    class ReadingSettingsBg(val bg_color: String)

    data class CheckIfBook (val check_book: Boolean)

    data class UserProfile(val name: String, val email: String)

    class ReadingSettingsText(val text_color: String, val text_size: Float,  val text_font: String)

    data class ReadingSettings(val bg_color: String, val text_color: String, val text_size: Float, val text_font: String)
}
