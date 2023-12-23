package com.example.foxbook.activities

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.foxbook.ClientAPI
import com.example.foxbook.ClientAPI.apiService
import com.example.foxbook.page.BookPageAdapter
import com.example.foxbook.R
import com.example.foxbook.api.BookPage
import com.example.foxbook.api.BookTextChunks
import com.example.foxbook.api.CheckIfBookInFavorites
import com.example.foxbook.api.Message
import com.example.foxbook.api.ReadingProgress
import com.example.foxbook.api.ReadingSettingsBg
import com.example.foxbook.api.ReadingSettingsText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.*
import kotlin.math.min

open class ReadingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pageArrayList: ArrayList<BookPage>

    lateinit var pageAdapter: BookPageAdapter

    private val processedItems = mutableSetOf<Int>()
    private var visiblePage = 0
    private lateinit var pageText: ArrayList<String>


    companion object {
        const val BOOK_ID = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        saveReadingProgressToAPI(visiblePage) // зберігання прогресу в бд
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading)
        val layoutReading: ConstraintLayout = findViewById(R.id.readingLayout)

        // змінюємо колір фону за налаштуваннями
        getReadingSettingsBg { bgColor ->

            when (bgColor) {
                "white" -> {
                    layoutReading.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

                }

                "grey" -> {
                    layoutReading.setBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.settings_grey_bg
                        )
                    )

                }

                "black" -> {
                    layoutReading.setBackgroundColor(ContextCompat.getColor(this, R.color.black))

                }

                else -> {
                    layoutReading.setBackgroundColor(ContextCompat.getColor(this, R.color.book_bg))
                }
            }
        }


        val bookId = intent.getIntExtra(BOOK_ID, -1)

        // кнопка назад
        val backButton: ImageButton = findViewById(R.id.imgBtnBackFromReading)
        backButton.setOnClickListener {
            finish()
        }

        // Отримання тексту книги
        getBookTextChunks(bookId) { pageTextChunks ->
            pageText = pageTextChunks


            recyclerView = findViewById(R.id.recyclerPageViewReading)
            val layoutManager = LinearLayoutManager(this)

            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(true)

            pageArrayList = arrayListOf()

            loadData()
            getReadingProgress(bookId) { readingProgress ->
                recyclerView.scrollToPosition(readingProgress)
            }
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {

                        val firstVisibleItem = layoutManager.findViewByPosition(
                            firstVisibleItemPosition
                        )
                        val visibleHeight = recyclerView.height - firstVisibleItem?.top!!
                        val itemHeight = firstVisibleItem.height ?: 1
                        visiblePage = (firstVisibleItemPosition + visibleHeight / itemHeight)
                        Log.e("RecyclerViewScroll", "visiblePage - $visiblePage")
                    }

                    for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
                        if (!processedItems.contains(i)) {
                            // Add the item to the set of processed items to avoid duplicates
                            processedItems.add(i)
                        }
                    }
                }


            })
        }

    }

    private fun loadData() {
        for (i in pageText) {
            val bookPageText = BookPage(i)
            pageArrayList.add(bookPageText)
        }
        recyclerView.adapter = BookPageAdapter(pageArrayList)
    }

    private fun getBookTextChunks(bookId: Int, callback: (ArrayList<String>) -> Unit) {
        val call = apiService.getBookTextChunks(bookId)

        call.enqueue(object : Callback<BookTextChunks> {
            override fun onResponse(
                call: Call<BookTextChunks>, response: Response<BookTextChunks>
            ) {
                if (response.isSuccessful) {
                    val textChunks = response.body()?.text_chunks
                    val chunksSize = textChunks?.size ?: 0
                    Log.d("Chunks Size", "Number of Chunks: $chunksSize")
                    textChunks?.forEachIndexed { index, chunk ->
                        Log.d("Chunk", "Chunk $index: $chunk")
                    }

                    callback(textChunks as ArrayList<String>)

                } else {

                    // обробка невдачі відповіді
                    Log.e(
                        "qwe", "Unsuccessful response getBookTextChunks: ${response.code()}"
                    )
                }
            }

            override fun onFailure(call: Call<BookTextChunks>, t: Throwable) {
                // обробка невдачі
                Log.e("qwe", "API request failed with exception", t)
            }
        })
    }

    private fun getReadingProgress(bookId: Int, callback: (Int) -> Unit) {
        val call = apiService.getReadingProgress(bookId)

        call.enqueue(object : Callback<ReadingProgress> {
            override fun onResponse(
                call: Call<ReadingProgress>,
                response: Response<ReadingProgress>
            ) {
                if (response.isSuccessful) {
                    Log.e("qwe", "response - $response")
                    Log.e("qwe", response.body()?.reading_progress.toString())

                    val readingProgress = response.body()?.reading_progress ?: 0
                    Log.e("qwe", "reading_progress - $readingProgress")
                    callback(readingProgress)
                } else {

                    Log.e("qwe", "Unsuccessful response getReadingProgress: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ReadingProgress>, t: Throwable) {
                Log.e("qwe", "API request failed with exception", t)
            }
        })
    }

    private fun saveReadingProgressToAPI(readingProgress: Int) {
        val bookId = intent.getIntExtra(BOOK_ID, -1)
        val call = apiService.updateReadingProgress(bookId, readingProgress)

        call.enqueue(object : Callback<Message> {
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if (response.isSuccessful) {
                    // усіх
                    Log.e("qwe", "Reading progress saved successfully")
                } else {
                    // обробка невдачі відповіді
                    Log.e(
                        "qwe",
                        "Unsuccessful response saveReadingProgressToAPI: ${response.code()}"
                    )
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                // обробка невдачі
                Log.e("qwe", "API request failed with exception", t)
            }
        })
    }
    private fun getReadingSettingsBg(callback: (String) -> Unit) {
        apiService.getReadingSettingsBg().enqueue(object : Callback<ReadingSettingsBg> {
            override fun onResponse(
                call: Call<ReadingSettingsBg>, response: Response<ReadingSettingsBg>
            ) {
                if (response.isSuccessful) {
                    val readingSettings = response.body()

                    // дістаємо значення
                    val bg_color = readingSettings?.bg_color

                    if (bg_color != null) {
                        callback(bg_color)
                    }

                } else {
                    // неуспішний запит
                    Log.e("qwe", "Failed to get reading settings: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ReadingSettingsBg>, t: Throwable) {
                // помилка мережі
                Log.e("qwe", "Network error: ${t.message}")
            }
        })
    }
}
