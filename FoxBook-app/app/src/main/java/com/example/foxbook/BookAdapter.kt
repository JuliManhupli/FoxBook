package com.example.foxbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BookAdapter(private val bookList: MutableList<Book>) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    var onItemClick: ((Book) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.book_item_layout, parent,
            false)
        return BookViewHolder((itemView))
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val currentItem = bookList[position]
        Glide.with(holder.bookCover.context).load(currentItem.cover).into(holder.bookCover)
        holder.bookTitle.text = currentItem.title
        holder.bookAuthor.text = currentItem.author
        holder.bookRating.text = currentItem.rating.toString()
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