package com.example.foxbook.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foxbook.R
import com.example.foxbook.api.Book

class RecommendationAdapter(private val itemList: MutableList<Book>) : RecyclerView.Adapter<RecommendationAdapter.RecommendViewHolder>()  {

    var onItemClick: ((Book) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.book_recommendation_layout, parent,
            false)
        return RecommendViewHolder((itemView))
    }

    override fun onBindViewHolder(
        holder: RecommendViewHolder,
        position: Int
    ) {
        val currentItem = itemList[position]
        Glide.with(holder.bookCover.context).load(currentItem.cover).into(holder.bookCover)
        holder.bookTitle.text = currentItem.title

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class RecommendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookCover: ImageView = itemView.findViewById(R.id.imgRecommendationCover)
        val bookTitle: TextView = itemView.findViewById(R.id.txtRecommendNameBook)
    }
}