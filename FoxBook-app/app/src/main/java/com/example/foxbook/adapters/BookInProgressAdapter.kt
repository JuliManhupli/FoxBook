package com.example.foxbook.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.foxbook.R
import com.example.foxbook.api.BookInProgress

class BookInProgressAdapter (private val bookInProgressList: MutableList<BookInProgress>) : RecyclerView.Adapter<BookInProgressAdapter.BookInProgressViewHolder>() {

    var onItemClick: ((BookInProgress) -> Unit)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookInProgressViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.book_in_progress_layout, parent,
            false)
        return BookInProgressViewHolder((itemView))
    }

    override fun onBindViewHolder(holder: BookInProgressViewHolder, position: Int) {
        val currentItem = bookInProgressList[position]
//        Glide.with(holder.bookCover.context).load(currentItem.cover).into(holder.bookCover)

        if (currentItem.cover != null) {
            Glide.with(holder.bookCover.context)
                .load(currentItem.cover)
                .placeholder(R.drawable.no_image) // Replace with your placeholder image
                .error(R.drawable.no_image) // Replace with your error image
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Handle the error here
                        Log.e("qwe", "Error loading image", e)
                        return false // Return false to allow the error placeholder to be shown
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Image successfully loaded
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


        val progress = currentItem.reading_progress + 1
        val pages = currentItem.pages
        holder.bookProgressBar.progress = progress
        holder.bookProgressBar.max = pages

        val readPercent = progress * 100 / pages
        holder.bookReadPercent.text = "$readPercent %"
        holder.itemView.setOnClickListener{
            onItemClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return bookInProgressList.size
    }


    class BookInProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val bookCover: ImageView = itemView.findViewById(R.id.imgBookInProgressCover)
        val bookTitle: TextView = itemView.findViewById(R.id.txtBookInProgressName)
        val bookAuthor: TextView = itemView.findViewById(R.id.txtBookInProgressAuthor)
        val bookRating: TextView = itemView.findViewById(R.id.txtBookInProgressRating)
        val bookReadPercent: TextView = itemView.findViewById(R.id.txtReadingPercentage)
        val bookProgressBar: SeekBar = itemView.findViewById(R.id.linearProgressIndicator)
    }
}