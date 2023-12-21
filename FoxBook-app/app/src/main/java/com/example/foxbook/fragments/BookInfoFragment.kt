package com.example.foxbook.fragments

import android.content.Intent
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.foxbook.ClientAPI
import com.example.foxbook.R
import com.example.foxbook.activities.ReadingActivity
import com.example.foxbook.api.Book
import com.example.foxbook.api.CheckIfBookInFavorites
import com.example.foxbook.api.Message
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BookInfoFragment : Fragment(R.layout.fragment_book_info) {

    private var targetFragment: String = ""

    companion object {
        fun newInstance(targetFragment: String): BookInfoFragment {
            val fragment = BookInfoFragment()
            fragment.targetFragment = targetFragment
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = requireArguments().getParcelable<Book>("android")
        Log.e("qwe", "data")
        Log.e("qwe", data.toString())


        val backButton: ImageButton = view.findViewById(R.id.imgBtnBackToSearch)

        backButton.setOnClickListener {
            if (targetFragment == SearchPageFragment::class.java.simpleName) {
                navigateToSearchPageFragment()
            } else if (targetFragment == FavouriteBooksFragment::class.java.simpleName) {
                navigateToFavoriteBooksFragment()
            }
        }


        if (data != null) {

            // кнопка "читати"
            val btnToReadingBook: Button = view.findViewById(R.id.btnToReading)

            btnToReadingBook.setOnClickListener {
                addBookToLibrary(data.id)
                val intent = Intent(activity, ReadingActivity::class.java)
                intent.putExtra(ReadingActivity.BOOK_ID, data.id)
                startActivity(intent)
            }

            val likeButton: ImageButton = view.findViewById(R.id.imgBtnUnliked)

            checkIfBookInFavorites(data.id) { isBookInFavorites ->
                requireActivity().runOnUiThread {
                    updateUI(isBookInFavorites)
                }
            }

            likeButton.setOnClickListener {
                checkIfBookInFavorites(data.id) { isBookInFavorites ->
                    if (isBookInFavorites) {
                        // Remove the book from favorites
                        removeBookFromFavorites(data.id)
                        likeButton.setImageResource(R.drawable.heart)
                    } else {
                        // Add the book to favorites
                        addBookToFavorites(data.id)
                        likeButton.setImageResource(R.drawable.heart_full)
                    }
                }
            }

            val coverImg: ImageView = view.findViewById(R.id.imgBookInfoCover)
            val titleView: TextView = view.findViewById(R.id.txtBookInfoTitle)
            val authorView: TextView = view.findViewById(R.id.txtBookInfoAuthor)
            val userRatingView: TextView = view.findViewById(R.id.txtBookInfoUserRating)
            val ratingView: TextView = view.findViewById(R.id.txtBookInfoRating)
            val genreView: TextView = view.findViewById(R.id.txtBookInfoGenre)
            val descView: TextView = view.findViewById(R.id.txtBookInfoDescription)


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

            titleView.text = data.title ?: "Назва невідома"
            authorView.text = data.author ?: "Автор невідомий"
            ratingView.text = (data.rating ?: "-").toString()
            genreView.text = data.genre ?: "-"
            descView.text = data.annotation ?: "Анотації немає"


            // Оцінювання користувачем книги
            val ratingBar: RatingBar = view.findViewById(R.id.ratingUserBar)

            // Якщо оцінка вже стоїть
            if (userRatingView.text == "2.0") {
                ratingBar.rating = 2.0F
            }

            // Зміна оцінки
            ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                userRatingView.text = rating.toString()
            }
        }
    }

    private fun navigateToSearchPageFragment() {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.flFragment, SearchPageFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToFavoriteBooksFragment() {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.flFragment, FavouriteBooksFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun updateUI(isBookInFavorites: Boolean) {
        val likeButton: ImageButton = view?.findViewById(R.id.imgBtnUnliked) ?: return

        if (isBookInFavorites) {
            likeButton.setImageResource(R.drawable.heart_full)
        } else {
            likeButton.setImageResource(R.drawable.heart)
        }
    }

    private fun checkIfBookInFavorites(bookId: Int, callback: (Boolean) -> Unit) {
        // Replace this with your actual logic to check if the book is in favorites
        // You may need to make an API call to check this on the server
        val call = ClientAPI.apiService.checkIfBookInFavorites(bookId)

        call.enqueue(object : Callback<CheckIfBookInFavorites> {
            override fun onResponse(
                call: Call<CheckIfBookInFavorites>,
                response: Response<CheckIfBookInFavorites>
            ) {


                if (response.isSuccessful) {

                    val isBookInFavorites = response.body()?.isInFavorites ?: false
                    callback(isBookInFavorites)
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
    }

    private fun addBookToFavorites(bookId: Int) {
        val call = ClientAPI.apiService.addToFavorites(bookId)

        call.enqueue(object : Callback<Message> {
            override fun onResponse(
                call: Call<Message>,
                response: Response<Message>
            ) {
                if (response.isSuccessful) {

                    val addRemoveFavoriteResponse = response.body()?.message
                    // Handle the response as needed
                    if (addRemoveFavoriteResponse == "Book added to favorites successfully") {
                        // Update UI or perform other actions
                        Toast.makeText(
                            requireContext(), "Книгу успішно додано до улюбленого!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(), "Помилка додавання!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Handle unsuccessful response
                    Log.e("qwe", "Unsuccessful response addBookToFavorites: ${response.code()}")
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun removeBookFromFavorites(bookId: Int) {
        val call = ClientAPI.apiService.removeFromFavorites(bookId)

        call.enqueue(object : Callback<Message> {
            override fun onResponse(
                call: Call<Message>,
                response: Response<Message>
            ) {
                if (response.isSuccessful) {
                    val addRemoveFavoriteResponse = response.body()?.message
                    // Handle the response as needed
                    if (addRemoveFavoriteResponse == "Book removed from favorites successfully") {
                        // Update UI or perform other actions
                        Toast.makeText(
                            requireContext(), "Книгу видалено з улюбленого!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(), "Помилка видалення!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Handle unsuccessful response
                    Log.e(
                        "qwe",
                        "Unsuccessful response removeBookFromFavorites: ${response.code()}"
                    )
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun addBookToLibrary(bookId: Int) {
        val call = ClientAPI.apiService.addBookToLibrary(bookId)

        call.enqueue(object : Callback<Message> {
            override fun onResponse(
                call: Call<Message>,
                response: Response<Message>
            ) {
                if (response.isSuccessful) {

                    val addBookToLibraryResponse = response.body()?.message
                    Log.e("qwe", "addBookToLibraryResponse - $addBookToLibraryResponse")
                    // Handle the response as needed
                    if (addBookToLibraryResponse == "Book added to library") {
                        // Update UI or perform other actions
                        Toast.makeText(
                            requireContext(), "Книгу додано до бібліотеки!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (addBookToLibraryResponse != "Book is already library") {
                        Toast.makeText(
                            requireContext(), "Помилка додавання!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Handle unsuccessful response
                    Log.e("qwe", "Unsuccessful response addBookToLibrary: ${response.code()}")
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}