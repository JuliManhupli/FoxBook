package com.example.foxbook.fragments

import android.os.Bundle
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

        likeButton.setOnClickListener{
            Toast.makeText(requireContext(), "Like!", Toast.LENGTH_SHORT).show()
        }

        val backButton: ImageButton = view.findViewById(R.id.imgBtnBackToSearch)

        backButton.setOnClickListener{
            val bookInfoFragment = SearchPageFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.flFragment, bookInfoFragment)
            transaction.commit()
        }

        val data = requireArguments().getParcelable<Book>("android")
        if (data != null) {
            val coverImg: ImageView = view.findViewById(R.id.imgBookInfoCover)
            val titleView: TextView = view.findViewById(R.id.txtBookInfoTitle)
            val authorView: TextView = view.findViewById(R.id.txtBookInfoAuthor)
            val ratingView: TextView = view.findViewById(R.id.txtBookInfoRating)
            val genreView: TextView = view.findViewById(R.id.txtBookInfoGenre)
            val descView: TextView = view.findViewById(R.id.txtBookInfoDescription)

            Glide.with(coverImg.context).load(data.cover).into(coverImg)
            titleView.text = data.title
            authorView.text = data.author
            ratingView.text = data.rating.toString()
            genreView.text = data.genre
            descView.text = data.description
        }
    }

}