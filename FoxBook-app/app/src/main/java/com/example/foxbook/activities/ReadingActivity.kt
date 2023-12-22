package com.example.foxbook.activities

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foxbook.ClientAPI
import com.example.foxbook.ClientAPI.apiService
import com.example.foxbook.page.BookPageAdapter
import com.example.foxbook.R
import com.example.foxbook.api.BookPage
import com.example.foxbook.api.CheckIfBookInFavorites
import com.example.foxbook.api.Message
import com.example.foxbook.api.ReadingProgress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.*
import kotlin.math.min

open class ReadingActivity : AppCompatActivity() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())


    private lateinit var recyclerView: RecyclerView
    private lateinit var pageAdapter: BookPageAdapter

    private var pageText: ArrayList<String> = ArrayList()
    private var totalNumberOfPages = 0
    private var currentPage = 0
    private val pageSize = 1000
    private var visiblePage = 0
    private lateinit var layoutManager: LinearLayoutManager // оголошення layoutManager
    private var lastVisiblePosition = 0

    companion object {
        const val BOOK_ID = ""
    }

    override fun onDestroy() {


        super.onDestroy()
        coroutineScope.cancel() // скасування корутини при закритті екрану
        Log.e("qwe", "page - $visiblePage")
        saveReadingProgressToAPI(visiblePage) // зберігання прогресу в бд
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading)
        val bookId = intent.getIntExtra(BOOK_ID, -1)
        Log.e("qwe", "bookId - $bookId")

        // кнопка назад
        val backButton: ImageButton = findViewById(R.id.imgBtnBackFromReading)
        backButton.setOnClickListener {
            finish()
        }

        // налаштування RecyclerView та Adapter
        recyclerView = findViewById(R.id.recyclerPageViewReading)
        layoutManager = LinearLayoutManager(this) // ініціалізація layoutManager
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        pageAdapter = BookPageAdapter()
        recyclerView.adapter = pageAdapter

        // Отримання тексту книги

        getReadingProgress(bookId) { readingProgress ->
            currentPage = readingProgress
            Log.e("qwe", "readingProgress - $readingProgress")
//            recyclerView.scrollToPosition(readingProgress)
            getBookText(bookId, readingProgress)
        }
//        getBookText(20)
    }




    private fun getBookText(bookId: Int, readingProgress: Int) {
        val call = apiService.getBookText(bookId)

        call.enqueue(object : Callback<BookPage> {
            override fun onResponse(call: Call<BookPage>, response: Response<BookPage>) {
                if (response.isSuccessful) {
                    val bookText = response.body()?.text
                    Log.e("qwe", "bookText.toString()")
                    if (bookText != null) {
                        pageText.add(bookText)

                        totalNumberOfPages = (bookText.length + pageSize - 1) / pageSize
                        Log.e("qwe", "totalNumberOfPages")
                        Log.e("qwe", totalNumberOfPages.toString())

                        // Load pages from 0 to readingProgress initially
                        loadPages(0, currentPage) {
                            Log.e("qwe", "1 - readingProgress")

                            Log.e("qwe", "3 - scrollToPosition")
                            // Load additional pages when the last item is visible
                            Log.e("qwe", "$currentPage")
                            loadNextPages()

                            // Setup scrolling for pagination
                            recyclerView.addOnScrollListener(object :
                                RecyclerView.OnScrollListener() {
                                override fun onScrolled(
                                    recyclerView: RecyclerView,
                                    dx: Int,
                                    dy: Int
                                ) {
                                    super.onScrolled(recyclerView, dx, dy)
                                    val firstVisibleItemPosition =
                                        layoutManager.findFirstVisibleItemPosition()
                                    if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {

                                        val firstVisibleItem =
                                            layoutManager.findViewByPosition(
                                                firstVisibleItemPosition
                                            )
                                        val visibleHeight =
                                            recyclerView.height - firstVisibleItem?.top!!
                                        val itemHeight =
                                            firstVisibleItem.height ?: 1
                                        visiblePage =
                                            (firstVisibleItemPosition + visibleHeight / itemHeight) + 1
                                        Log.e("qwe", "visiblePage - $visiblePage")
                                    }

                                    val lastVisibleItemPosition =
                                        layoutManager.findLastVisibleItemPosition()
                                    val totalItemCount = layoutManager.itemCount

                                    if (currentPage * pageSize < pageText[0].length) {
                                        if (lastVisibleItemPosition == totalItemCount - 1) {
                                            // Load additional pages when the last item is visible
                                            loadNextPages()
                                        }
                                    }
                                }
                            })
                        }
                    }
                } else {
                    // Handle unsuccessful response
                    Log.e("qwe", "Unsuccessful response getBookText: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<BookPage>, t: Throwable) {
                // Handle failure
                Log.e("qwe", "API request failed with exception", t)
            }
        })
    }

    private fun loadPages(start: Int, end: Int, callback: () -> Unit) {
        Log.e("qwe", "2 - loadPages")
        Log.e("qwe", "$end")
        coroutineScope.launch {
            // Execute the following code on the IO dispatcher
            withContext(Dispatchers.IO) {
                if (pageText.isNotEmpty()) {
                    val textLength = pageText[0].length

                    // Ensure the start and end positions are within the valid range
                    val actualStart = min(start * pageSize, textLength)
                    val actualEnd = min(end * pageSize, textLength)

                    // Check if there are pages to load
                    if (actualStart < actualEnd) {
                        // Retrieve the text for the specified pages
                        val pagesText = pageText[0].substring(actualStart, actualEnd)

                        // Switch back to the main dispatcher to update the UI
                        withContext(Dispatchers.Main) {
                            // Add the pages to the adapter
                            pageAdapter.addPage(pagesText)
                            // Scroll to the last visible position after adding the pages
//                            recyclerView.scrollToPosition(currentPage)
                            recyclerView.scrollToPosition(end)
                            callback()
                        }
                    }
                }
            }
        }
    }

    private fun loadNextPages() {
        coroutineScope.launch {
            // Execute the following code on the IO dispatcher
            withContext(Dispatchers.IO) {
                // Calculate the end position of the next page
                val end = (currentPage + 1) * pageSize

                // Retrieve the text for the next page
                val textLength = if (pageText.isNotEmpty()) pageText[0].length else 0
                val actualEnd = min(end, textLength)

                // Ensure the indices are within the valid range
                val actualStart = currentPage * pageSize
                val actualEndSubstring = min(actualEnd, textLength)

                if (actualStart < actualEndSubstring) {
                    val nextPageText = pageText[0].substring(actualStart, actualEndSubstring)

                    // Update the current page
                    currentPage++

                    // Switch back to the main dispatcher to update the UI
                    withContext(Dispatchers.Main) {
                        // Add the next page to the adapter
                        pageAdapter.addPage(nextPageText)
                    }
                }
            }
        }
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
}
