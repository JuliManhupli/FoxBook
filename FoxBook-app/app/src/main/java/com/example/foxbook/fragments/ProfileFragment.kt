package com.example.foxbook.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.foxbook.ClientAPI
import com.example.foxbook.R
import com.example.foxbook.api.Book
import com.example.foxbook.api.BooksResponse
import com.example.foxbook.api.CheckIfBookInFavorites
import com.example.foxbook.api.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment(R.layout.fragment_profile) {


//    private lateinit var txtProfileName: TextView
//    private lateinit var txtProfileEmail: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtProfileName: TextView = view.findViewById(R.id.txtProfileName)
        val txtProfileEmail: TextView = view.findViewById(R.id.txtProfileEmail)

        getUserProfileInfo { profileInfo ->


            txtProfileName.text = profileInfo?.name ?: ""
            txtProfileEmail.text = profileInfo?.email ?: ""

        }

        val favouriteBooksButton: ConstraintLayout = view.findViewById(R.id.likedBooks)
        favouriteBooksButton.setOnClickListener {
            val favouriteBooksFragment = FavouriteBooksFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.flFragment, favouriteBooksFragment)
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
}