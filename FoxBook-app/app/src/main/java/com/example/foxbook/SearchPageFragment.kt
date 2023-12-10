package com.example.foxbook

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class SearchPageFragment : Fragment(R.layout.fragment_search_page) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookArrayList: ArrayList<Book>

    private lateinit var searchView: SearchView
    private lateinit var searchList: ArrayList<Book>

    lateinit var bookAdapter: BookAdapter

    lateinit var coverURL: Array<String>
    lateinit var titleName: Array<String>
    lateinit var authorName: Array<String>
    lateinit var ratingScore: Array<Double>
    lateinit var genreName: Array<String>
    lateinit var descriptionText: Array<String>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filterButton: ImageButton = view.findViewById(R.id.imgButtonFiltering)

        filterButton.setOnClickListener{
            Toast.makeText(requireContext(), "Фільтрування!", Toast.LENGTH_SHORT).show()
        }

        val sortingButton: ImageButton = view.findViewById(R.id.imgButtonSorting)

        sortingButton.setOnClickListener{
            Toast.makeText(requireContext(), "Сортування!", Toast.LENGTH_SHORT).show()
        }

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
            "https://books.google.com/books/content?id=kQS7AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        )

        titleName = arrayOf(
            "Книга",
            "Щось",
            "Що",
            "Велике щось",
            "Very good",
            "BookName6",
            "Кудись",
            "BookName8",
            "Навіщо",
            "BookName10"
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
            "AuthorName10"
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
            "AuthorName10"
        )

        ratingScore = arrayOf(1.5, 2.0, 3.0, 40.0, 5.0, 6.0, 7.5, 8.0, 9.0, 10.0)

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
            "GenreName10"
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
        )

        recyclerView = view.findViewById(R.id.search_recycler_view)
        searchView = view.findViewById(R.id.searchUpperBar)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        bookArrayList = arrayListOf()
        searchList = arrayListOf()
        dataInitialise()


        searchView.clearFocus()
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()){
                    bookArrayList.forEach {
                        if (it.title.toLowerCase(Locale.getDefault()).contains(searchText)){
                            searchList.add(it)
                        }
                    }
                    recyclerView.adapter!!.notifyDataSetChanged()
                } else {
                    searchList.clear()
                    searchList.addAll(bookArrayList)
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }

        })

        bookAdapter = BookAdapter(searchList)
        recyclerView.adapter = bookAdapter

        bookAdapter.onItemClick = {
            val bookInfoFragment = BookInfoFragment()

            val bundle = Bundle()
            bundle.putParcelable("android", it)
            bookInfoFragment.arguments = bundle

            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.flFragment, bookInfoFragment)
            transaction.commit()
        }
    }

    private fun dataInitialise(){
        for (i in coverURL.indices){
            val book = Book(coverURL[i],
                titleName[i],
                authorName[i],
                ratingScore[i],
                genreName[i],
                descriptionText[i])
            bookArrayList.add(book)
        }
        searchList.addAll(bookArrayList)
        recyclerView.adapter = BookAdapter(searchList)
    }

}