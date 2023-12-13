package com.example.foxbook

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.foxbook.api.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var txtProfileName: TextView
    private lateinit var txtProfileEmail: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtProfileName = view.findViewById(R.id.txtProfileName)
        txtProfileEmail = view.findViewById(R.id.txtProfileEmail)

        // Assuming you have a function to retrieve the access token
        val accessToken = getAccessToken()

        // Make a Retrofit API call to get user profile
        val requestCall = ClientAPI.apiService.getUserProfile("Bearer $accessToken")

        requestCall.enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {
                    val userProfile = response.body()

                    // Update UI with user profile data
                    txtProfileName.text = userProfile?.name ?: ""
                    txtProfileEmail.text = userProfile?.email ?: ""
                } else {
                    // Handle API error
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                // Handle network error
            }
        })
    }

    // Replace this function with your actual implementation to retrieve the access token
    private fun getAccessToken(): String {
        val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString("access_token", "") ?: ""
    }
}
