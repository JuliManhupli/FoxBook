package com.example.foxbook.adapters

import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foxbook.ClientAPI.apiService
import com.example.foxbook.R
import com.example.foxbook.api.BookApi
import com.example.foxbook.api.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BookPageAdapter(private val pageList: ArrayList<BookApi.BookPage>): RecyclerView.Adapter<BookPageAdapter.PageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.page_item, parent,
            false)
        return PageViewHolder((itemView))
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        getReadingSettingsAndApply(holder)
        val currentItem = pageList[position]
        holder.bookPage.text = currentItem.text

    }

    override fun getItemCount(): Int {
        return pageList.size
    }

    class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val bookPage: TextView = itemView.findViewById(R.id.mText)
    }


    private fun getReadingSettingsAndApply(holder: PageViewHolder) {
        apiService.getReadingSettingsText().enqueue(object : Callback<UserData.ReadingSettingsText> {
            override fun onResponse(
                call: Call<UserData.ReadingSettingsText>,
                response: Response<UserData.ReadingSettingsText>
            ) {
                if (response.isSuccessful) {
                    val readingSettings = response.body()

                    // дістаємо значення
                    val textColor = readingSettings?.text_color
                    val textSize = readingSettings?.text_size
                    val textFont = readingSettings?.text_font

                    // змінюємо налаштування
                    holder.bookPage.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize ?: 44.0F)
                    if (textColor == "white") {
                        holder.bookPage.setTextColor(
                            holder.bookPage.context.resources.getColor(
                                textColor?.toIntOrNull() ?: R.color.white
                            )
                        )
                    } else {
                        holder.bookPage.setTextColor(
                            holder.bookPage.context.resources.getColor(
                                textColor?.toIntOrNull() ?: R.color.black
                            )
                        )
                    }
                    holder.bookPage.typeface = Typeface.create(textFont ?: "inter", Typeface.NORMAL)
                } else {
                    // неуспішний запит
                    Log.e("SYSTEM_ERROR", "Failed to get reading settings: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserData.ReadingSettingsText>, t: Throwable) {
                // помилка мережі
                Log.e("SYSTEM_ERROR", "Network error: ${t.message}")
            }
        })
    }
}

