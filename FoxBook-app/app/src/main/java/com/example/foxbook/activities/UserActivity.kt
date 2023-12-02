package com.example.foxbook.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.foxbook.HomePageFragment
import com.example.foxbook.ProfileFragment
import com.example.foxbook.R
import com.example.foxbook.ReadingInProgressFragment
import com.example.foxbook.SearchPageFragment

class UserActivity : AppCompatActivity() {
    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val homePage = HomePageFragment()
        val searchPage = SearchPageFragment()
        val redingToDo = ReadingInProgressFragment()
        val profilePage = ProfileFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, homePage)
            commit()
        }

        // Пошук кнопки авторизації за айді
        val toHomePage: Button = findViewById(R.id.button3)

        // За натиском робимо перевіряємо дані
        toHomePage.setOnClickListener{
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, homePage)
                commit()
            }
        }

        val toSearchPage: Button = findViewById(R.id.button4)

        // За натиском робимо перевіряємо дані
        toSearchPage.setOnClickListener{
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, searchPage)
                commit()
            }
        }
    }
}