package com.example.foxbook.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.foxbook.fragments.ProfileFragment
import com.example.foxbook.R
import com.example.foxbook.fragments.HomePageFragment
import com.example.foxbook.fragments.ReadingInProgressFragment
import com.example.foxbook.fragments.SearchPageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class UserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        //фрагменти
        val homeFragment = HomePageFragment()
        val searchFragment = SearchPageFragment()
        val inProgressFragment = ReadingInProgressFragment()
        val profileFragment = ProfileFragment()

        // початкова сторінка
        setCurrentFragment(homeFragment)

        val buttonNavView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // переключання між екранми меню
        buttonNavView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.miHome -> setCurrentFragment(homeFragment)
                R.id.miSearch -> setCurrentFragment(searchFragment)
                R.id.miReadProgress -> setCurrentFragment(inProgressFragment)
                R.id.miProfile -> setCurrentFragment(profileFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            addToBackStack(null)
            commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}