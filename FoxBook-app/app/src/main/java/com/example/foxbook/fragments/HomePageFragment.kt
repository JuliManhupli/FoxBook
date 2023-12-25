package com.example.foxbook.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.foxbook.ClientAPI
import com.example.foxbook.R
import com.example.foxbook.adapters.RecommendationAdapter
import com.example.foxbook.api.Book
import com.example.foxbook.api.BookInProgress
import com.example.foxbook.api.BookToRead
import com.example.foxbook.api.Recommendations
import com.example.foxbook.api.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePageFragment : Fragment(R.layout.fragment_home_page) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookRecommendArrayList: MutableList<Book>

    lateinit var bookRecommendAdapter: RecommendationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerRecommendView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = LinearLayoutManager(context,
            RecyclerView.HORIZONTAL, false)


        val txtProfileName: TextView = view.findViewById(R.id.helloUser)

        getUserProfileInfo { profileInfo ->
            Log.d("BOKTOREAD", profileInfo.toString())
            txtProfileName.text = "Вітаємо, ${profileInfo?.name}!" ?: "Вітаємо!"
        }

        // продовжити читати
        getBookToRead { bookToRead ->
            if (bookToRead != null) {
                val book = bookToRead[0]
                Log.d("BOKTOREAD", book.toString())

                putData(book)

                val bookLayout: CardView = view.findViewById(R.id.cardViewBookToRead)

                bookLayout.setOnClickListener {
                    val bookInfoFragment =
                        BookInfoFragment.newInstance(SearchPageFragment::class.java.simpleName)
                    val bundle = Bundle()
                    bundle.putParcelable("android", book)
                    bookInfoFragment.arguments = bundle

                    val transaction: FragmentTransaction =
                        requireFragmentManager().beginTransaction()
                    transaction.replace(R.id.flFragment, bookInfoFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
            else {
                Log.d("READINGS", "Не було отримано книгу!!")
            }
        }


        // реокомендації
        bookRecommendArrayList = mutableListOf()
        dataInitialise { recommendations ->
            if (recommendations != null) {
                bookRecommendArrayList.addAll(recommendations)
                bookRecommendAdapter = RecommendationAdapter(bookRecommendArrayList)
                recyclerView.adapter = bookRecommendAdapter


                bookRecommendAdapter.onItemClick = {
                    val bookInfoFragment =
                        BookInfoFragment.newInstance(SearchPageFragment::class.java.simpleName)
                    val bundle = Bundle()
                    bundle.putParcelable("android", it)
                    bookInfoFragment.arguments = bundle

                    val transaction: FragmentTransaction =
                        requireFragmentManager().beginTransaction()
                    transaction.replace(R.id.flFragment, bookInfoFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
            else {
                Toast.makeText( requireContext(), "Не було отримано рекомендації!!", Toast.LENGTH_SHORT
                    ).show()
            }
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
                    Toast.makeText(requireContext(), "Не отримано дані імені!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення профілю!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun putData(currentItem: BookInProgress){
        val bookCover: ImageView = requireView().findViewById(R.id.imgBookContinueReadCover)
        val bookTitle: TextView = requireView().findViewById(R.id.txtBookContinueReadName)
        val bookAuthor: TextView = requireView().findViewById(R.id.bookContinueReadAuthor)
        val bookRating: TextView = requireView().findViewById(R.id.bookContinueReadRating)
        val bookReadPercent: TextView = requireView().findViewById(R.id.txtBookContinueReadProgress)
        val bookProgressBar: SeekBar = requireView().findViewById(R.id.seekBarProgressContinueRead)

        if (currentItem.cover != null) {
            Glide.with(bookCover.context)
                .load(currentItem.cover)
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
                .into(bookCover)
        } else {
            bookCover.setImageResource(R.drawable.no_image)
        }

        bookTitle.text = currentItem.title
        bookAuthor.text = currentItem.author
        bookRating.text = if (currentItem.rating == -1.0) "-" else currentItem.rating.toString()


        val progress = currentItem.reading_progress + 1
        val pages = currentItem.pages
        bookProgressBar.progress = progress
        bookProgressBar.max = pages

        val readPercent = progress * 100 / pages
        Log.e("readPercent", "readPercent: $readPercent")
        bookReadPercent.text = "$readPercent %"
    }

    private fun getBookToRead(callback: (List<BookInProgress>?) -> Unit){
        val requestCall = ClientAPI.apiService.continueReading()

        requestCall.enqueue(object : Callback<BookToRead> {
            override fun onResponse(call: Call<BookToRead>, response: Response<BookToRead>) {
                if (response.isSuccessful) {
                    val booksResponse = response.body()
                    val bookToContinue = booksResponse?.book_to_read
                    Log.d("BOOKTOREAD", booksResponse.toString())
                    Log.d("BOOKTOREAD", bookToContinue.toString())
                    if (!bookToContinue.isNullOrEmpty()) {
                        Log.d("BOOKTOREAD", bookToContinue.toString())
                        callback(bookToContinue)
                    } else {
                        val cardBook: CardView = requireView().findViewById(R.id.cardViewBookToRead)
                        val textReading: TextView = requireView().findViewById(R.id.txtForReading)
                        cardBook.visibility = View.GONE
                        textReading.visibility = View.GONE

                        val foxImg: ImageView = requireView().findViewById(R.id.noReadingsFox)
                        foxImg.visibility = View.VISIBLE
                        Log.e("BOOKTOREAD", "Books list is null")
                        callback(null)
                    }
                } else {
                    if (response.code() == 404) {
                        Log.e("BOOKTOREAD", "Data not found (404)")
                        callback(null)
                    } else {
                        Log.e("BOOKTOREAD", "Unsuccessful response: ${response.code()}")
                        Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                            .show()
                        callback(null)
                    }
                }
            }
            override fun onFailure(call: Call<BookToRead>, t: Throwable) {
                Log.e("BOOKTOREAD", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення з книг!", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }


    private fun dataInitialise(callback: (List<Book>?) -> Unit){
        val requestCall = ClientAPI.apiService.getRecommendations()

        requestCall.enqueue(object : Callback<Recommendations> {
            override fun onResponse(call: Call<Recommendations>, response: Response<Recommendations>) {
                if (response.isSuccessful) {
                    val booksResponse = response.body()
                    val books = booksResponse?.recommendations
                    if (!books.isNullOrEmpty()) {
                        Log.d("RECOMMEND", books.toString())
                        callback(books)
                    } else {
                        Toast.makeText(requireContext(), "Рекомендацій немає!", Toast.LENGTH_SHORT).show()
                        callback(null)
                    }
                } else {
                    if (response.code() == 404) {
                        Log.e("RECOMMEND", "Data not found (404)")
                        callback(null)
                    } else {
                        Toast.makeText(requireContext(), "Не отримано дані рекомендацій!", Toast.LENGTH_SHORT)
                            .show()
                        callback(null)
                    }
                }
            }
            override fun onFailure(call: Call<Recommendations>, t: Throwable) {
                Toast.makeText(requireContext(), "Помилка підключення рекомендацій!", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }

}