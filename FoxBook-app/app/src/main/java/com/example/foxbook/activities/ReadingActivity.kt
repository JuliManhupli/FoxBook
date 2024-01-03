package com.example.foxbook.activities
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foxbook.ClientAPI.apiService
import com.example.foxbook.adapters.BookPageAdapter
import com.example.foxbook.R
import com.example.foxbook.api.BookApi
import com.example.foxbook.api.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class ReadingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pageArrayList: ArrayList<BookApi.BookPage>

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
                    val btnBackFromReading: ImageButton = findViewById(R.id.imgBtnBackFromReading)
                    btnBackFromReading.setImageResource(R.drawable.baseline_arrow_white_24)
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
                    }

                    for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
                        if (!processedItems.contains(i)) {
                            // Додаємо елемент до оброблених, щоб недопустити повторвів
                            processedItems.add(i)
                        }
                    }
                }
            })
        }
    }

    private fun loadData() {
        for (i in pageText) {
            val bookPageText = BookApi.BookPage(i)
            pageArrayList.add(bookPageText)
        }
        recyclerView.adapter = BookPageAdapter(pageArrayList)
    }

    private fun getBookTextChunks(bookId: Int, callback: (ArrayList<String>) -> Unit) {
        val call = apiService.getBookTextChunks(bookId)

        call.enqueue(object : Callback<BookApi.BookTextChunks> {
            override fun onResponse(
                call: Call<BookApi.BookTextChunks>, response: Response<BookApi.BookTextChunks>
            ) {
                if (response.isSuccessful) {
                    val textChunks = response.body()?.text_chunks
                    textChunks?.forEachIndexed { index, chunk ->
                    }

                    callback(textChunks as ArrayList<String>)

                } else {
                    // обробка невдачі відповіді
                    Toast.makeText(this@ReadingActivity, "Помилка отримання тексту!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BookApi.BookTextChunks>, t: Throwable) {
                // обробка невдачі
                Toast.makeText(this@ReadingActivity, "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getReadingProgress(bookId: Int, callback: (Int) -> Unit) {
        val call = apiService.getReadingProgress(bookId)

        call.enqueue(object : Callback<BookApi.ReadingProgress> {
            override fun onResponse(
                call: Call<BookApi.ReadingProgress>,
                response: Response<BookApi.ReadingProgress>
            ) {
                if (response.isSuccessful) {
                    val readingProgress = response.body()?.reading_progress ?: 0
                    callback(readingProgress)
                } else {
                    Toast.makeText(this@ReadingActivity, "Помилка отримання прогресу!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BookApi.ReadingProgress>, t: Throwable) {
                Toast.makeText(this@ReadingActivity, "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveReadingProgressToAPI(readingProgress: Int) {
        val bookId = intent.getIntExtra(BOOK_ID, -1)
        val call = apiService.updateReadingProgress(bookId, readingProgress)

        call.enqueue(object : Callback<UserData.Message> {
            override fun onResponse(call: Call<UserData.Message>, response: Response<UserData.Message>) {
                if (response.isSuccessful) {
                    // усіх
                    Toast.makeText(this@ReadingActivity, "Прогрес читання збережено!", Toast.LENGTH_SHORT).show()
                } else {
                    // обробка невдачі відповіді
                    Toast.makeText(this@ReadingActivity, "Помилка збереження читання!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserData.Message>, t: Throwable) {
                // обробка невдачі
                Toast.makeText(this@ReadingActivity, "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getReadingSettingsBg(callback: (String) -> Unit) {
        apiService.getReadingSettingsBg().enqueue(object : Callback<UserData.ReadingSettingsBg> {
            override fun onResponse(
                call: Call<UserData.ReadingSettingsBg>, response: Response<UserData.ReadingSettingsBg>
            ) {
                if (response.isSuccessful) {
                    val readingSettings = response.body()

                    // дістаємо значення
                    val bg_color = readingSettings?.bg_color

                    if (bg_color != null) {
                        callback(bg_color)
                    }

                } else {
                    // обробка невдалої відповіді
                    Toast.makeText(this@ReadingActivity, "Помилка отримання фону читання!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserData.ReadingSettingsBg>, t: Throwable) {
                // обробка невдалого підключення
                Toast.makeText(this@ReadingActivity, "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
