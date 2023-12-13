package com.example.foxbook

import Genre
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView

class GenreAdapter(private val genreList: ArrayList<Genre>): RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    var onItemClick: ((Genre) -> Unit)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenreViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.genre_item_layout, parent,
            false)
        return GenreAdapter.GenreViewHolder((itemView))
    }

    override fun onBindViewHolder(holder: GenreAdapter.GenreViewHolder, position: Int) {
        val currentItem = genreList[position]
        holder.filterBtn.text = currentItem.genre
    }

    override fun getItemCount(): Int {
        return genreList.size
    }

    class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val filterBtn: AppCompatButton = itemView.findViewById(R.id.btnFilterName)
    }

}