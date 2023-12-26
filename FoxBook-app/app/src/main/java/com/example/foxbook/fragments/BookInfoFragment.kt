package com.example.foxbook.fragments

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.foxbook.ClientAPI.apiService
import com.example.foxbook.R
import com.example.foxbook.activities.ReadingActivity
import com.example.foxbook.api.Book
import com.example.foxbook.api.BookInProgress
import com.example.foxbook.api.CheckIfBook

import com.example.foxbook.api.Message
import com.example.foxbook.api.UserRating
import okhttp3.internal.notify
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


const val CHANNEL_ID = "channelid"
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
        Log.e("qwe", "onViewCreated")

        val data: Parcelable? = if (targetFragment == ReadingInProgressFragment::class.java.simpleName) {
            requireArguments().getParcelable<BookInProgress>("android")
        } else {
            requireArguments().getParcelable<Book>("android")
        }

        val backButton: ImageButton = view.findViewById(R.id.imgBtnBackToSearch)

        backButton.setOnClickListener {
            when (targetFragment) {
                HomePageFragment::class.java.simpleName -> navigateTo(HomePageFragment())
                SearchPageFragment::class.java.simpleName -> navigateTo(SearchPageFragment())
                FavouriteBooksFragment::class.java.simpleName -> navigateTo(FavouriteBooksFragment())
                ReadingInProgressFragment::class.java.simpleName -> navigateTo(ReadingInProgressFragment())
                HomePageFragment::class.java.simpleName -> navigateTo(HomePageFragment())
            }
        }


        data?.let {
            when (it) {
                is Book -> setupBookViews(it, view)
                is BookInProgress -> setupBookInProgressViews(it, view)
            }
        }
    }

    private fun setupBookViews(book: Book, view: View) {
        Log.e("qwe", "book")

        val btnToReadingBook: Button = view.findViewById(R.id.btnToReading)

        btnToReadingBook.setOnClickListener {
            addBookToLibrary(book.id)
            val intent = Intent(activity, ReadingActivity::class.java)
            intent.putExtra(ReadingActivity.BOOK_ID, book.id)
            startActivity(intent)
        }



        val likeButton: ImageButton = view.findViewById(R.id.imgBtnUnliked)

        checkIfBookInFavorites(book.id) { isBookInFavorites ->
            requireActivity().runOnUiThread {
                updateUI(isBookInFavorites, likeButton)
            }
        }

        likeButton.setOnClickListener {
            checkIfBookInFavorites(book.id) { isBookInFavorites ->
                if (isBookInFavorites) {
                    // Remove the book from favorites
                    removeBookFromFavorites(book.id)
                    likeButton.setImageResource(R.drawable.heart)
                } else {
                    // Add the book to favorites
                    addBookToFavorites(book.id)
                    likeButton.setImageResource(R.drawable.heart_full)

                    // сповіщення
                    createNotificationChannel()

                    val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                    builder.setSmallIcon(R.mipmap.ic_profile)
                        .setContentTitle("Маєте чудовий смак :)")
                        .setContentText("Книгу '${book.title}' було додано в Улюблене!")
                        .priority = NotificationCompat.PRIORITY_DEFAULT

                    with(NotificationManagerCompat.from(requireContext())) {
                        if (ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            return@checkIfBookInFavorites
                        }
                        notify(1, builder.build())
                    }
                }
            }
        }

        val coverImg: ImageView = view.findViewById(R.id.imgBookInfoCover)
        val titleView: TextView = view.findViewById(R.id.txtBookInfoTitle)
        val authorView: TextView = view.findViewById(R.id.txtBookInfoAuthor)
        val ratingView: TextView = view.findViewById(R.id.txtBookInfoRating)
        val genreView: TextView = view.findViewById(R.id.txtBookInfoGenre)
        val descView: TextView = view.findViewById(R.id.txtBookInfoDescription)


        if (book.cover != null) {
            Glide.with(coverImg.context)
                .load(book.cover)
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

        titleView.text = book.title ?: "Назва невідома"
        authorView.text = book.author ?: "Автор невідомий"
        Log.e("qwe", "book.rating - $book.rating")
        ratingView.text = if (book.rating == -1.0) "-" else book.rating.toString()
        genreView.text = book.genre ?: "-"
        descView.text = book.annotation ?: "Анотації немає"

        val btnDeleteFromReading: ImageButton = view.findViewById(R.id.imgBtnDeleteFromReading)

        checkIfBookInLibrary(book.id) { isBookInLibrary ->
            if (isBookInLibrary) {
                btnDeleteFromReading.visibility = View.VISIBLE
            }

        }
        btnDeleteFromReading.setOnClickListener {
            removeBookFromLibrary(book.id)
            btnDeleteFromReading.visibility = View.GONE
        }

        getUserRatings(book.id) {userRating ->

            // Оцінювання користувачем книги
            val ratingBar: RatingBar = view.findViewById(R.id.ratingUserBar)
            val textRatingUser: TextView = view.findViewById(R.id.txtRatingUser)
            val userRatingView: TextView = view.findViewById(R.id.txtBookInfoUserRating)
            val imgBookStarUser: ImageView = view.findViewById(R.id.imgBookStarUser)

            if (userRating == -2.0) {
                // Приховати виставлення оцінки
                ratingBar.visibility = View.GONE
                textRatingUser.visibility = View.GONE
                userRatingView.visibility = View.GONE
                imgBookStarUser.visibility = View.GONE
            } else {
                // Встановлення значення оцінки
                userRatingView.text = if (userRating == -1.0) "-" else userRating.toString()
                ratingBar.rating = userRating.toFloat()
            }

            // Зміна оцінки
            ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                userRatingView.text = rating.toString()
                Log.e("qwe", "* - $rating")
                Log.e("qwe", "*2 - ${userRatingView.text}")
                updateUserRating(book.id, rating.toDouble())
            }
        }
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Улюблені книги",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Додати книгу в улюблене"

            val notificationManager = ContextCompat.getSystemService(
                requireContext(),
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setupBookInProgressViews(bookInProgress: BookInProgress, view: View) {
        val btnToReadingBook: Button = view.findViewById(R.id.btnToReading)

        btnToReadingBook.setOnClickListener {
            addBookToLibrary(bookInProgress.id)
            val intent = Intent(activity, ReadingActivity::class.java)
            intent.putExtra(ReadingActivity.BOOK_ID, bookInProgress.id)
            startActivity(intent)
        }

        val likeButton: ImageButton = view.findViewById(R.id.imgBtnUnliked)

        checkIfBookInFavorites(bookInProgress.id) { isBookInFavorites ->
            requireActivity().runOnUiThread {
                updateUI(isBookInFavorites, likeButton)
            }
        }

        likeButton.setOnClickListener {
            checkIfBookInFavorites(bookInProgress.id) { isBookInFavorites ->
                if (isBookInFavorites) {
                    // Remove the book from favorites
                    removeBookFromFavorites(bookInProgress.id)
                    likeButton.setImageResource(R.drawable.heart)
                } else {
                    // Add the book to favorites
                    addBookToFavorites(bookInProgress.id)
                    likeButton.setImageResource(R.drawable.heart_full)
                }
            }
        }

        val coverImg: ImageView = view.findViewById(R.id.imgBookInfoCover)
        val titleView: TextView = view.findViewById(R.id.txtBookInfoTitle)
        val authorView: TextView = view.findViewById(R.id.txtBookInfoAuthor)
        val ratingView: TextView = view.findViewById(R.id.txtBookInfoRating)
        val genreView: TextView = view.findViewById(R.id.txtBookInfoGenre)
        val descView: TextView = view.findViewById(R.id.txtBookInfoDescription)


        if (bookInProgress.cover != null) {
            Glide.with(coverImg.context)
                .load(bookInProgress.cover)
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

        titleView.text = bookInProgress.title ?: "Назва невідома"
        authorView.text = bookInProgress.author ?: "Автор невідомий"
        ratingView.text = if (bookInProgress.rating == -1.0) "-" else bookInProgress.rating.toString()
        genreView.text = bookInProgress.genre ?: "-"
        descView.text = bookInProgress.annotation ?: "Анотації немає"

        val btnDeleteFromReading: ImageButton = view.findViewById(R.id.imgBtnDeleteFromReading)

        checkIfBookInLibrary(bookInProgress.id) { isBookInLibrary ->
            if (isBookInLibrary) {
                btnDeleteFromReading.visibility = View.VISIBLE
            }

        }
        btnDeleteFromReading.setOnClickListener {
            removeBookFromLibrary(bookInProgress.id)
            btnDeleteFromReading.visibility = View.GONE
        }

        getUserRatings(bookInProgress.id) {userRating ->

            // Оцінювання користувачем книги
            val ratingBar: RatingBar = view.findViewById(R.id.ratingUserBar)
            val textRatingUser: TextView = view.findViewById(R.id.txtRatingUser)
            val userRatingView: TextView = view.findViewById(R.id.txtBookInfoUserRating)
            val imgBookStarUser: ImageView = view.findViewById(R.id.imgBookStarUser)

            if (userRating == -2.0) {
                // Приховати виставлення оцінки
                ratingBar.visibility = View.GONE
                textRatingUser.visibility = View.GONE
                userRatingView.visibility = View.GONE
                imgBookStarUser.visibility = View.GONE
            } else {
                // Встановлення значення оцінки
                userRatingView.text = if (userRating == -1.0) "-" else userRating.toString()
                ratingBar.rating = userRating.toFloat()
            }

            // Зміна оцінки
            ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                userRatingView.text = rating.toString()
                updateUserRating(bookInProgress.id, rating.toDouble())
            }

        }

    }

    private fun navigateTo(fragment: Fragment) {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.flFragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun updateUI(isBookInFavorites: Boolean, likeButton: ImageButton) {
        likeButton.setImageResource(if (isBookInFavorites) R.drawable.heart_full else R.drawable.heart)
    }

    private fun checkIfBookInFavorites(bookId: Int, callback: (Boolean) -> Unit) {
        val call = apiService.checkIfBookInFavorites(bookId)

        call.enqueue(object : Callback<CheckIfBook> {
            override fun onResponse(
                call: Call<CheckIfBook>,
                response: Response<CheckIfBook>
            ) {
                if (response.isSuccessful) {
                    val isBookInFavorites = response.body()?.check_book ?: false
                    callback(isBookInFavorites)
                } else {
                    // обробка невдалої відповіді
                    Log.e("qwe", "Unsuccessful response checkIfBookInFavorites: ${response.code()}")
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<CheckIfBook>, t: Throwable) {
                // обробка невдалого підключення
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addBookToFavorites(bookId: Int) {
        val call = apiService.addToFavorites(bookId)

        call.enqueue(object : Callback<Message> {
            override fun onResponse(
                call: Call<Message>,
                response: Response<Message>
            ) {
                if (response.isSuccessful) {
                    val responseMessage = response.body()?.message
                    Toast.makeText(requireContext(), responseMessage, Toast.LENGTH_SHORT).show()
                } else {
                    // обробка невдалої відповіді
                    Log.e("qwe", "Unsuccessful response addBookToFavorites: ${response.code()}")
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                // обробка невдалого підключення
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun removeBookFromFavorites(bookId: Int) {
        val call = apiService.removeFromFavorites(bookId)

        call.enqueue(object : Callback<Message> {
            override fun onResponse(
                call: Call<Message>,
                response: Response<Message>
            ) {
                if (response.isSuccessful) {
                    val responseMessage = response.body()?.message
                    Toast.makeText(requireContext(), responseMessage, Toast.LENGTH_SHORT).show()
                } else {
                    // обробка невдалої відповіді
                    Log.e("qwe","Unsuccessful response removeBookFromFavorites: ${response.code()}")
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                // обробка невдалого підключення
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserRatings(bookId: Int, callback: (Double) -> Unit) {
        val call = apiService.getUserRating(bookId)

        call.enqueue(object : Callback<UserRating> {
            override fun onResponse(
                call: Call<UserRating>,
                response: Response<UserRating>
            ) {
                if (response.isSuccessful) {
                    val userRating = response.body()?.user_rating ?: -2
                    callback(userRating.toDouble())
                } else {
                    // обробка невдалої відповіді
                    Log.e("qwe", "Unsuccessful response addBookToLibrary: ${response.code()}")
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<UserRating>, t: Throwable) {
                // обробка невдалого підключення
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUserRating(bookId: Int, userRating: Double) {
        val call = apiService.updateUserRating(bookId, userRating)

        call.enqueue(object : Callback<Message> {
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if (response.isSuccessful) {
                    val responseMessage = response.body()?.message
                    Log.e("qwe", "updatedLibraryEntry - ${responseMessage}")
                    Toast.makeText(requireContext(), responseMessage, Toast.LENGTH_SHORT).show()
                } else {
                    // обробка невдалої відповіді
                    Log.e("qwe", "Unsuccessful response updateUserRating: ${response.code()}")
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                // обробка невдалого підключення
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkIfBookInLibrary(bookId: Int, callback: (Boolean) -> Unit) {
        val call = apiService.checkIfBookInLibrary(bookId)

        call.enqueue(object : Callback<CheckIfBook> {
            override fun onResponse(
                call: Call<CheckIfBook>,
                response: Response<CheckIfBook>
            ) {
                if (response.isSuccessful) {
                    val isBookInLibrary = response.body()?.check_book ?: false
                    callback(isBookInLibrary)
                } else {
                    // обробка невдалої відповіді
                    Log.e("qwe", "Unsuccessful response checkIfBookInFavorites: ${response.code()}")
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<CheckIfBook>, t: Throwable) {
                // обробка невдалого підключення
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addBookToLibrary(bookId: Int) {
        val call = apiService.addBookToLibrary(bookId)

        call.enqueue(object : Callback<Message> {
            override fun onResponse(
                call: Call<Message>,
                response: Response<Message>
            ) {
                if (response.isSuccessful) {
                    val responseMessage = response.body()?.message
                    if (responseMessage != "") {
                        Toast.makeText(requireContext(), responseMessage, Toast.LENGTH_SHORT).show()
                    }

                } else {
                    // обробка невдалої відповіді
                    Log.e("qwe", "Unsuccessful response addBookToLibrary: ${response.code()}")
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                // обробка невдалого підключення
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun removeBookFromLibrary(bookId: Int) {
        val call = apiService.removeBookFromLibrary(bookId)

        call.enqueue(object : Callback<Message> {
            override fun onResponse(
                call: Call<Message>,
                response: Response<Message>
            ) {
                if (response.isSuccessful) {
                    val responseMessage = response.body()?.message
                    Log.d("qwe", responseMessage.toString())
                    Toast.makeText(requireContext(), responseMessage, Toast.LENGTH_SHORT).show()
                } else {
                    // обробка невдалої відповіді
                    Log.e("qwe", "Unsuccessful response addBookToLibrary: ${response.code()}")
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                // обробка невдалого підключення
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}