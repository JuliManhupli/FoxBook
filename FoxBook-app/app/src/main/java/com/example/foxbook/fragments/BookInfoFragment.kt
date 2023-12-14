package com.example.foxbook.fragments

import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.foxbook.R
import com.example.foxbook.api.Book


class BookInfoFragment : Fragment(R.layout.fragment_book_info) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val likeButton: ImageButton = view.findViewById(R.id.imgBtnUnliked)

        likeButton.setOnClickListener {
            Toast.makeText(requireContext(), "Like!", Toast.LENGTH_SHORT).show()
        }

        val backButton: ImageButton = view.findViewById(R.id.imgBtnBackToSearch)

        backButton.setOnClickListener {
            val bookInfoFragment = SearchPageFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.flFragment, bookInfoFragment)
            transaction.commit()
        }

        val data = requireArguments().getParcelable<Book>("android")
        Log.e("qwe", "data")
        Log.e("qwe", data.toString())
        if (data != null) {
            val coverImg: ImageView = view.findViewById(R.id.imgBookInfoCover)
            val titleView: TextView = view.findViewById(R.id.txtBookInfoTitle)
            val authorView: TextView = view.findViewById(R.id.txtBookInfoAuthor)
            val ratingView: TextView = view.findViewById(R.id.txtBookInfoRating)
            val genreView: TextView = view.findViewById(R.id.txtBookInfoGenre)
            val descView: TextView = view.findViewById(R.id.txtBookInfoDescription)
            Log.e("qwe", "coverImg")
            Log.e("qwe", coverImg.toString())

            if (data.cover != null) {
                Glide.with(coverImg.context)
                    .load(data.cover)
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
                    .into(coverImg)
            } else {
                coverImg.setImageResource(R.drawable.no_image)
            }

            Log.d("qwe", coverImg.context.toString())
            Log.d("qwe", coverImg.context.toString())
            titleView.text = data.title ?: "Назва невідома"
            authorView.text = data.author ?: "Автор невідомий"
            ratingView.text = data.rating.toString()
            genreView.text = data.genre ?: "-"
            descView.text = data.annotation ?: "Анотації немає"
        }
    }
}