package com.example.foxbook.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
        if (currentItem.cover != null) {
            Glide.with(holder.bookCover.context)
                .load(currentItem.cover)
                .placeholder(R.drawable.no_image) // Вставте тимчасову обкладинку
                .error(R.drawable.no_image) // Змініть на тимчасову
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Помилка завантаження
                        Log.e("SYSTEM_ERROR", "Error loading image", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Обкладинка завантажилася
                        return false
                    }
                })
                .into(holder.bookCover)
        } else {
            holder.bookCover.setImageResource(R.drawable.no_image)
        }

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