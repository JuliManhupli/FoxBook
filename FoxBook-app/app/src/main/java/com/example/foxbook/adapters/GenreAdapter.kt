package com.example.foxbook.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foxbook.R
import com.example.foxbook.api.Genre

class GenreAdapter(private val genreList: ArrayList<Genre>): RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    var onItemClick: ((Genre, AppCompatButton) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenreViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.genre_item_layout, parent,
            false)
        return GenreViewHolder((itemView))
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val currentItem = genreList[position]
        holder.filterBtn.text = currentItem.genre
        if (currentItem.isSelected) {
            holder.filterBtn.setBackgroundResource(R.color.chosen_filter)
            holder.filterBtn.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        } else {
            holder.filterBtn.setBackgroundResource(R.color.white)
            holder.filterBtn.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
        }
        holder.filterBtn.setOnClickListener {
            onItemClick?.invoke(currentItem, holder.filterBtn)
        }
    }

    override fun getItemCount(): Int {
        return genreList.size
    }

    class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val filterBtn: AppCompatButton = itemView.findViewById(R.id.btnFilterName)
    }

}