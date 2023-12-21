package com.example.foxbook.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foxbook.BookAdapter
import com.example.foxbook.R
import com.example.foxbook.RecommendationAdapter
import com.example.foxbook.api.Book

class HomePageFragment : Fragment(R.layout.fragment_home_page) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookRecommendArrayList: MutableList<Book>

    lateinit var bookRecommendAdapter: RecommendationAdapter

    lateinit var coverURL: Array<String>
    lateinit var titleName: Array<String>
    lateinit var authorName: Array<String>
    lateinit var ratingScore: Array<Double>
    lateinit var genreName: Array<String>
    lateinit var descriptionText: Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coverURL = arrayOf(
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api",
        )

        titleName = arrayOf(
            "Книгааааааааааааааааааааааааааааааааааааа",
            "Щось",
            "Що",
            "Велике щось",
            "Very good",
            "BookName6",
            "Кудись",
            "BookName8",
            "Навіщо",
            "BookName10",
            "Книга",
            "Щось",
            "Що",
            "Велике щось",
            "Very good",
            "BookName6",
            "Кудись",
            "BookName8",
            "Навіщо",
            "BookName10",
        )

        authorName = arrayOf(
            "AuthorName1",
            "AuthorName2",
            "AuthorName3",
            "AuthorName4",
            "AuthorName5",
            "AuthorName6",
            "AuthorName7",
            "AuthorName8",
            "AuthorName9",
            "AuthorName10",
            "AuthorName1",
            "AuthorName2",
            "AuthorName3",
            "AuthorName4",
            "AuthorName5",
            "AuthorName6",
            "AuthorName7",
            "AuthorName8",
            "AuthorName9",
            "AuthorName10",
        )

        ratingScore = arrayOf(
            1.5, 2.0, 3.0, 40.0, 5.0, 6.0, 7.5, 8.0, 9.0, 10.0,
            1.5, 2.0, 3.0, 40.0, 5.0, 6.0, 7.5, 8.0, 9.0, 10.0,
        )

        genreName = arrayOf(
            "GenreName1",
            "GenreName2",
            "GenreName3",
            "GenreName4",
            "GenreName5",
            "GenreName6",
            "GenreName7",
            "GenreName8",
            "GenreName9",
            "GenreName10",
            "GenreName1",
            "GenreName2",
            "GenreName3",
            "GenreName4",
            "GenreName5",
            "GenreName6",
            "GenreName7",
            "GenreName8",
            "GenreName9",
            "GenreName10",
        )

        descriptionText = arrayOf(
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
            getString(R.string.some_text),
        )


        recyclerView = view.findViewById(R.id.recyclerRecommendView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = LinearLayoutManager(context,
            RecyclerView.HORIZONTAL, false)

        bookRecommendArrayList = mutableListOf()
        dataInitialise()
    }


    private fun dataInitialise(){
        for (i in coverURL.indices){
            val book = Book(i,
                coverURL[i],
                titleName[i],
                authorName[i],
                ratingScore[i],
                genreName[i],
                descriptionText[i])
            bookRecommendArrayList.add(book)
        }
        recyclerView.adapter = RecommendationAdapter(bookRecommendArrayList)
    }

}