package com.example.foxbook.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.foxbook.R
import com.example.foxbook.activities.UserActivity
import com.example.foxbook.api.Book

class BookAdapter(private val bookList: MutableList<Book>) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    var onItemClick: ((Book) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.book_item_layout, parent,
            false)
        return BookViewHolder((itemView))
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val currentItem = bookList[position]

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
                        // Помилка
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
        holder.bookAuthor.text = currentItem.author
        holder.bookRating.text = if (currentItem.rating == -1.0) "-" else currentItem.rating.toString()
        holder.bookGenre.text = currentItem.genre

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(currentItem)
        }
    }

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val bookCover: ImageView = itemView.findViewById(R.id.imgBookCover)
        val bookTitle: TextView = itemView.findViewById(R.id.txtBookName)
        val bookAuthor: TextView = itemView.findViewById(R.id.txtBookAuthor)
        val bookRating: TextView = itemView.findViewById(R.id.txtBookRating)
        val bookGenre: TextView = itemView.findViewById(R.id.txtBookGenre)
    }
}