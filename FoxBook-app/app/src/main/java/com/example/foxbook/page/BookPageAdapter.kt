package com.example.foxbook.page

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foxbook.R
import com.example.foxbook.api.BookPage


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

        fun bind(pageText: String) {
            contentTextView.text = pageText
        }
    }
}
