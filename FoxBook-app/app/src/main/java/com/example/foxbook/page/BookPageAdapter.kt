package com.example.foxbook.page

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foxbook.ClientAPI.apiService
import com.example.foxbook.R
import com.example.foxbook.api.BookPage
import com.example.foxbook.api.ReadingSettingsText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BookPageAdapter : RecyclerView.Adapter<BookPageAdapter.PageViewHolder>() {

    private val pages: MutableList<String> = mutableListOf()

    fun addPage(pageText: String) {
        pages.add(pageText)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.page_item, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {

        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size

    class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.mText)

        @SuppressLint("ResourceType")
        fun bind(pageText: String) {
            getReadingSettingsAndApply(pageText)
        }

        private fun getReadingSettingsAndApply(pageText: String) {
            apiService.getReadingSettingsText().enqueue(object : Callback<ReadingSettingsText> {
                override fun onResponse(
                    call: Call<ReadingSettingsText>,
                    response: Response<ReadingSettingsText>
                ) {
                    if (response.isSuccessful) {
                        val readingSettings = response.body()

                        // дістаємо значення
                        val textColor = readingSettings?.text_color
                        val textSize = readingSettings?.text_size
                        val textFont = readingSettings?.text_font

                        // змінюємо налаштування
                        contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize ?: 44.0F)
                        if (textColor == "white") {
                            contentTextView.setTextColor(contentTextView.context.resources.getColor(
                            textColor?.toIntOrNull() ?: R.color.white))
                        } else {
                            contentTextView.setTextColor(contentTextView.context.resources.getColor(
                            textColor?.toIntOrNull() ?: R.color.black))
                        }

                        contentTextView.typeface = Typeface.create(textFont ?: "inter", Typeface.NORMAL)

                        // додаємо текст
                        contentTextView.text = pageText

                    } else {
                        // неуспішний запит
                        Log.e("qwe", "Failed to get reading settings: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ReadingSettingsText>, t: Throwable) {
                    // помилка мережі
                    Log.e("qwe", "Network error: ${t.message}")
                }
            })
        }
    }
}




