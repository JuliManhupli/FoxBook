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


class BookPageAdapter(private val pageList: ArrayList<BookPage>): RecyclerView.Adapter<BookPageAdapter.PageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.page_item, parent,
            false)
        return PageViewHolder((itemView))
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val currentItem = pageList[position]
        holder.bookPage.text = currentItem.text
    }

    override fun getItemCount(): Int {
        return pageList.size
    }

    class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val bookPage: TextView = itemView.findViewById(R.id.mText)
    }
}