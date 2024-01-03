package com.example.foxbook.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foxbook.databinding.ActivityMainBinding
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.example.foxbook.TokenManager

// Перевірка до доступу інтернету
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork
    val capabilities =
        connectivityManager.getNetworkCapabilities(network)
    return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (TokenManager.getAccessToken().isNotEmpty()) {
            // Користувача авторизовано
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish() // Завершуємо нинішню актівіті
        }


        binding.btnStart.setOnClickListener {
            if (isNetworkAvailable(this)) {

                // Перевірка авторизації користувача
                if (TokenManager.getAccessToken().isNotEmpty()) {
                    // Користувача авторизовано
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    finish() // Завершуємо актівіті
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }


            } else {
                // Нема інтернет підключення
                Toast.makeText(
                    this,
                    "Немає підключення до Інтернету!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}