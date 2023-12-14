package com.example.foxbook.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
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
    private var limit = 5

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        page = 1

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
        getAllBooks(page) { booksFromApi ->
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
                    val bookInfoFragment = BookInfoFragment()
                    val bundle = Bundle()
                    bundle.putParcelable("android", it)
                    bookInfoFragment.arguments = bundle

                    val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
                    transaction.replace(R.id.flFragment, bookInfoFragment)
                    transaction.commit()
                }
            } else {
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

        val start = page * limit
        val end = (page + 1) * limit

        getAllBooks(page) { newBooks ->
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
                // Handle the case when data retrieval fails
                Log.e("qwe", "Failed to retrieve more data from the API")
                Log.e("qwe", page.toString())
            }

            isLoading = false
            progressBar.visibility = View.GONE
        }
    }
    private fun getAllBooks(page: Int, callback: (List<Book>?) -> Unit) {
        val requestCall = ClientAPI.apiService.getBooks(page)

        requestCall.enqueue(object : Callback<BooksResponse> {
            override fun onResponse(call: Call<BooksResponse>, response: Response<BooksResponse>) {
                if (response.isSuccessful) {
                    val booksResponse = response.body()
                    val books = booksResponse?.results
                    if (books != null) {
                        Log.d("qwe", books.toString())
                        callback(books)
                    } else {
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
