package com.example.foxbook.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.foxbook.APIServices
import com.example.foxbook.ClientAPI
import com.example.foxbook.R
import com.example.foxbook.activities.LoginActivity
import com.example.foxbook.api.UserProfile
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment(R.layout.fragment_profile) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // вихід з акаунту
        val exitBtn: Button = view.findViewById(R.id.btnExit)
        exitBtn.setOnClickListener {
            logoutUser()
        }


        val txtProfileName: TextView = view.findViewById(R.id.txtProfileName)
        val txtProfileEmail: TextView = view.findViewById(R.id.txtProfileEmail)

        getUserProfileInfo { profileInfo ->


            txtProfileName.text = profileInfo?.name ?: ""
            txtProfileEmail.text = profileInfo?.email ?: ""

        }

        // до улюблених книг
        val favouriteBooksButton: CardView = view.findViewById(R.id.likedBooks)
        favouriteBooksButton.setOnClickListener {
            val favouriteBooksFragment = FavouriteBooksFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.flFragment, favouriteBooksFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // до налаштувань
        val toSettings: CardView = view.findViewById(R.id.toReadingSettings)
        toSettings.setOnClickListener {
            val settingsFragment = SettingsFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.flFragment, settingsFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun getUserProfileInfo(callback: (UserProfile?) -> Unit) {
        val call = ClientAPI.apiService.getUserProfile()

        call.enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {

                    val profileInfo = response.body()
                    Log.e("qwe", "profileInfo")
                    Log.e("qwe", profileInfo.toString())
                    callback(profileInfo)
                } else {
                    // Handle unsuccessful response
                    Log.e("qwe", "Unsuccessful response getUserProfileInfo: ${response.code()}")
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun logoutUser(){
        val call = ClientAPI.apiService.logout()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Помилка виходу!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

}