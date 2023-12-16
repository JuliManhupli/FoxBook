package com.example.foxbook.fragments

import android.content.Context
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.foxbook.ClientAPI
import com.example.foxbook.R
import com.example.foxbook.api.AddRemoveFavorite
import com.example.foxbook.api.Book
import com.example.foxbook.api.CheckIfBookInFavorites
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale



class BookInfoFragment : Fragment(R.layout.fragment_book_info) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = requireArguments().getParcelable<Book>("android")
        Log.e("qwe", "data")
        Log.e("qwe", data.toString())

        val likeButton: ImageButton = view.findViewById(R.id.imgBtnUnliked)

        likeButton.setOnClickListener {

            if (data != null) {
                // Check if the book is in favorites
                val isBookInFavorites = checkIfBookInFavorites(data.id)
                Log.e("qwe", "isBookInFavorites")
                Log.e("qwe", isBookInFavorites.toString())
                if (isBookInFavorites) {
                    // Remove the book from favorites
                    removeBookFromFavorites(data.id)
                    Toast.makeText(requireContext(), "Removed from favorites!", Toast.LENGTH_SHORT).show()
                } else {
                    // Add the book to favorites
                    addBookToFavorites(data.id)
                    Toast.makeText(requireContext(), "Added to favorites!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val backButton: ImageButton = view.findViewById(R.id.imgBtnBackToSearch)

        backButton.setOnClickListener {
            val bookInfoFragment = SearchPageFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.flFragment, bookInfoFragment)
            transaction.commit()
        }


        if (data != null) {
            val coverImg: ImageView = view.findViewById(R.id.imgBookInfoCover)
            val titleView: TextView = view.findViewById(R.id.txtBookInfoTitle)
            val authorView: TextView = view.findViewById(R.id.txtBookInfoAuthor)
            val ratingView: TextView = view.findViewById(R.id.txtBookInfoRating)
            val genreView: TextView = view.findViewById(R.id.txtBookInfoGenre)
            val descView: TextView = view.findViewById(R.id.txtBookInfoDescription)
            Log.e("qwe", "coverImg")
            Log.e("qwe", coverImg.toString())

            if (data.cover != null) {
                Glide.with(coverImg.context)
                    .load(data.cover)
                    .placeholder(R.drawable.no_image) // Replace with your placeholder image
                    .error(R.drawable.no_image) // Replace with your error image
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            // Handle the error here
                            Log.e("qwe", "Error loading image", e)
                            return false // Return false to allow the error placeholder to be shown
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            // Image successfully loaded
                            return false
                        }
                    })
                    .into(coverImg)
            } else {
                coverImg.setImageResource(R.drawable.no_image)
            }

            Log.d("qwe", coverImg.context.toString())
            Log.d("qwe", coverImg.context.toString())
            titleView.text = data.title ?: "Назва невідома"
            authorView.text = data.author ?: "Автор невідомий"
            ratingView.text = data.rating.toString()
            genreView.text = data.genre ?: "-"
            descView.text = data.annotation ?: "Анотації немає"
        }
    }

    private fun checkIfBookInFavorites(bookId: Int): Boolean {
        // Replace this with your actual logic to check if the book is in favorites
        // You may need to make an API call to check this on the server
        val call = ClientAPI.apiService.checkIfBookInFavorites(bookId)

        var isBookInFavorites = false

        call.enqueue(object : Callback<CheckIfBookInFavorites> {
            override fun onResponse(
                call: Call<CheckIfBookInFavorites>,
                response: Response<CheckIfBookInFavorites>
            ) {

                Log.e("qwe", "response checkIfBookInFavorites")
                Log.e("qwe", response.toString())
                if (response.isSuccessful) {
                    isBookInFavorites = response.body()?.isInFavorites ?: false
                    Log.e("qwe", "checkIfBookInFavorites")
                    Log.e("qwe", isBookInFavorites.toString())
                } else {
                    // Handle unsuccessful response
                    Log.e("qwe", "Unsuccessful response checkIfBookInFavorites: ${response.code()}")
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<CheckIfBookInFavorites>, t: Throwable) {
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })

        return isBookInFavorites
    }

    private fun addBookToFavorites(bookId: Int) {
        val call = ClientAPI.apiService.addToFavorites(bookId)

        call.enqueue(object : Callback<AddRemoveFavorite> {
            override fun onResponse(
                call: Call<AddRemoveFavorite>,
                response: Response<AddRemoveFavorite>
            ) {
                if (response.isSuccessful) {
                    val addRemoveFavoriteResponse = response.body()
                    Log.e("qwe", "addRemoveFavoriteResponse")
                    Log.e("qwe", addRemoveFavoriteResponse.toString())
                    // Handle the response as needed
                    if (addRemoveFavoriteResponse != null) {
                        // Update UI or perform other actions
                        Toast.makeText(
                            requireContext(),
                            "Book added to favorites successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Handle null response
                    }
                } else {
                    // Handle unsuccessful response
                    Log.e("qwe", "Unsuccessful response addBookToFavorites: ${response.code()}")
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<AddRemoveFavorite>, t: Throwable) {
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun removeBookFromFavorites(bookId: Int) {
        val call = ClientAPI.apiService.removeFromFavorites(bookId)

        call.enqueue(object : Callback<AddRemoveFavorite> {
            override fun onResponse(
                call: Call<AddRemoveFavorite>,
                response: Response<AddRemoveFavorite>
            ) {
                if (response.isSuccessful) {
                    val addRemoveFavoriteResponse = response.body()

                    // Handle the response as needed
                    if (addRemoveFavoriteResponse != null) {
                        // Update UI or perform other actions
                        Toast.makeText(
                            requireContext(),
                            "Book removed from favorites successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Handle null response
                    }
                } else {
                    // Handle unsuccessful response
                    Log.e("qwe", "Unsuccessful response removeBookFromFavorites: ${response.code()}")
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<AddRemoveFavorite>, t: Throwable) {
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }


    // Replace this function with your actual implementation to retrieve the access token
    private fun getAccessToken(): String {
        val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString("access_token", "") ?: ""
    }
}