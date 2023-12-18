package com.example.foxbook.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foxbook.databinding.ActivityMainBinding
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import com.example.foxbook.TokenManager

// Function to check if the device has internet connectivity
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
            // User is authenticated, redirect to the appropriate activity (e.g., UserActivity)
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish() // finish the current activity to prevent going back to the login screen
        }


        binding.btnStart.setOnClickListener {
            if (isNetworkAvailable(this)) {

                // Check if the user is already authenticated
                if (TokenManager.getAccessToken().isNotEmpty()) {
                    // User is authenticated, redirect to the appropriate activity (e.g., UserActivity)
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    finish() // finish the current activity to prevent going back to the login screen
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }


            } else {
                // No internet connection, show a message to the user
                Toast.makeText(
                    this,
                    "Немає підключення до Інтернету!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}