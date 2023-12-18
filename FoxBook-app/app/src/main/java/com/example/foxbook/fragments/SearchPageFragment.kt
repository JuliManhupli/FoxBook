package com.example.foxbook.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foxbook.BookAdapter
import com.example.foxbook.ClientAPI
import com.example.foxbook.R
import com.example.foxbook.api.Book
import com.example.foxbook.api.BooksResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class SearchPageFragment : Fragment(R.layout.fragment_search_page) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookArrayList: MutableList<Book>

    private lateinit var searchView: SearchView
    private lateinit var searchList: ArrayList<Book>

    lateinit var bookAdapter: BookAdapter

    private var page = 1
    private var isLoading = false

    private var selectedGenres: List<String> = emptyList()
    private var selectedAuthors: String? = null
    private var selectedSorting: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        page = 1
        val args = arguments
        if (args != null) {
            selectedGenres = args.getStringArrayList("selectedGenres") ?: emptyList()
            selectedAuthors = args.getString("selectedAuthors")
            selectedSorting = args.getString("selectedSorting")
        }

        val filterButton: ImageButton = view.findViewById(R.id.imgButtonFiltering)

        filterButton.setOnClickListener{
            val filtersFragment = FiltersFragment.newInstance(SearchPageFragment::class.java.simpleName)
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.flFragment, filtersFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        recyclerView = view.findViewById(R.id.search_recycler_view)
        searchView = view.findViewById(R.id.searchUpperBar)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        bookArrayList = arrayListOf()
        searchList = arrayListOf()

        loadData()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val pastVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val total: Int = bookAdapter.itemCount

                if (!isLoading && (visibleItemCount + pastVisibleItem) >= total) {
                    loadMoreData()
                }

                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun loadData() {
        val query = buildFilterQuery(selectedGenres, selectedAuthors, selectedSorting)
        val progressBar: ProgressBar = requireView().findViewById(R.id.progressBarSearch)

        getAllBooks(page, query) { booksFromApi ->
            if (booksFromApi != null) {
                page++
                bookArrayList.addAll(booksFromApi)
                searchList.addAll(bookArrayList)
                recyclerView.adapter = BookAdapter(searchList)

                searchView.clearFocus()
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchView.clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        searchList.clear()
                        val searchText = newText!!.toLowerCase(Locale.getDefault())
                        if (searchText.isNotEmpty()) {
                            bookArrayList.forEach {
                                if (it.title.toLowerCase(Locale.getDefault()).contains(searchText)) {
                                    searchList.add(it)
                                }
                            }
                            recyclerView.adapter?.notifyDataSetChanged()
                        } else {
                            searchList.clear()
                            searchList.addAll(bookArrayList)
                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                        return false
                    }
                })

                bookAdapter = BookAdapter(searchList)
                recyclerView.adapter = bookAdapter

                bookAdapter.onItemClick = {
                    val bookInfoFragment = BookInfoFragment.newInstance(SearchPageFragment::class.java.simpleName)
                    val bundle = Bundle()
                    bundle.putParcelable("android", it)
                    bookInfoFragment.arguments = bundle

                    val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
                    transaction.replace(R.id.flFragment, bookInfoFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            } else {
                // To hide the ProgressBar
                progressBar.visibility = View.GONE
                // Handle the case when data retrieval fails
                Log.e("qwe", "Failed to retrieve data from the API")
                Log.e("qwe", page.toString())
            }
        }
    }

    private fun loadMoreData() {
        isLoading = true

        val progressBar: ProgressBar = requireView().findViewById(R.id.progressBarSearch)
        progressBar.visibility = View.VISIBLE
        val query = buildFilterQuery(selectedGenres, selectedAuthors, selectedSorting)
        getAllBooks(page, query) { newBooks ->
            if (newBooks != null) {
                bookArrayList.addAll(newBooks)
                searchList.addAll(newBooks)

                if (::bookAdapter.isInitialized) {
                    bookAdapter.notifyDataSetChanged()
                } else {
                    recyclerView.adapter = BookAdapter(searchList)
                }

                page++
            } else {
                // To hide the ProgressBar
                progressBar.visibility = View.GONE
                // Handle the case when data retrieval fails
                Log.e("qwe", "Failed to retrieve more data from the API")
                Log.e("qwe", page.toString())
            }

            isLoading = false
            progressBar.visibility = View.GONE
        }
    }

    private fun buildFilterQuery(genres: List<String>, author: String?, sorting: String?): Map<String, String> {
        val queryMap = mutableMapOf<String, String>()

        if (genres.isNotEmpty()) {
            queryMap["genres"] = genres.joinToString(",")
        }

        Log.d("qwe", genres.toString())
        Log.d("qwe", "genres.toString()")

        if (!author.isNullOrBlank()) {
            queryMap["author"] = author
        }

        if (!sorting.isNullOrBlank()) {
            queryMap["sorting"] = sorting
        }

        return queryMap
    }

    private fun getAllBooks(page: Int, filterQuery: Map<String, String>, callback: (List<Book>?) -> Unit) {
        val requestCall = ClientAPI.apiService.getBooks(page, filterQuery)

        requestCall.enqueue(object : Callback<BooksResponse> {
            override fun onResponse(call: Call<BooksResponse>, response: Response<BooksResponse>) {
                if (response.isSuccessful) {
                    val booksResponse = response.body()
                    val books = booksResponse?.results
                    Log.d("qwe", "books.toString()")
                    Log.d("qwe", books.toString())
                    if (!books.isNullOrEmpty()) {
                        Log.d("qwe", books.toString())
                        callback(books)
                    } else {
                        Toast.makeText(requireContext(), "Даних немає!",Toast.LENGTH_SHORT).show()
                        Log.e("qwe", "Books list is null")
                        callback(null)
                    }
                } else {
                    if (response.code() == 404) {
                        // Handle 404 response (data not found) here
                        Log.e("qwe", "Data not found (404)")
                        callback(null)
                    } else {
                        // Handle unsuccessful response
                        Log.e("qwe", "Unsuccessful response: ${response.code()}")
                        Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                            .show()
                        callback(null)
                    }
                }
            }
            override fun onFailure(call: Call<BooksResponse>, t: Throwable) {
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }
}