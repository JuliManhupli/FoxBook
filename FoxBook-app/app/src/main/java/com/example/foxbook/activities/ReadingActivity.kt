package com.example.foxbook.activities

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foxbook.ClientAPI.apiService
import com.example.foxbook.page.BookPageAdapter
import com.example.foxbook.R
import com.example.foxbook.api.BookPage
import com.example.foxbook.api.Message
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.*

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
        getBookText(bookId)
//        getBookText(20)
    }

    private fun loadNextPages() {
        coroutineScope.launch {
            // виконати наступний код на диспетчері IO
            withContext(Dispatchers.IO) {
                // рахування кінця наступної сторінки
                val end = (currentPage + 1) * pageSize

                // витягнути текст для наступної сторінки
                val nextPageText = if (end < pageText[0].length) {
                    pageText[0].substring(currentPage * pageSize, end)
                } else {
                    pageText[0].substring(currentPage * pageSize)
                }

                // оновити поточну сторінку
                currentPage++
                Log.e("qwe", "с - $currentPage")

                // переключитися назад на головний диспетчер для оновлення UI
                withContext(Dispatchers.Main) {
                    // додати наступну сторінку до адаптера
                    pageAdapter.addPage(nextPageText)
                }
            }
        }
    }

    private fun getBookText(bookId: Int) {
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

                        // завантаження початкових сторінок
                        loadNextPages()

                        // налаштування прокрутки для пагінації
                        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                super.onScrolled(recyclerView, dx, dy)
                                val firstVisibleItemPosition =
                                    layoutManager.findFirstVisibleItemPosition()
                                if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {

                                    // страшні маніпуляції щоб вирахувати сторінку на якій зараз користувач
                                    val firstVisibleItem =
                                        layoutManager.findViewByPosition(firstVisibleItemPosition)
                                    val visibleHeight =
                                        recyclerView.height - firstVisibleItem?.top!!
                                    val itemHeight =
                                        firstVisibleItem.height ?: 1
                                    visiblePage =
                                        (firstVisibleItemPosition + visibleHeight / itemHeight) + 1


                                }


                                val lastVisibleItemPosition =
                                    layoutManager.findLastVisibleItemPosition()
                                val totalItemCount = layoutManager.itemCount

                                if (currentPage * pageSize < pageText[0].length) {
                                    if (lastVisibleItemPosition == totalItemCount - 1) {
                                        // завантаження додаткових сторінок, коли останній елемент видимий
                                        loadNextPages()
                                    }
                                }
                            }
                        })
                    }
                } else {
                    // обробка невдачі відповіді
                    Log.e("qwe", "Unsuccessful response getBookText: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<BookPage>, t: Throwable) {
                // обробка невдачі
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
