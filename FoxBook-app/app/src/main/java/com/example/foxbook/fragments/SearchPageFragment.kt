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
import com.example.foxbook.adapters.BookAdapter
import com.example.foxbook.ClientAPI
import com.example.foxbook.R
import com.example.foxbook.api.Book
import com.example.foxbook.api.BookApi
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
            transaction.addToBackStack("$filtersFragment")
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
                        val searchText = newText?.toLowerCase(Locale.getDefault()) ?: ""
                        val filteredList = if (searchText.isBlank()) {
                            // якщо пошук порожній, показати всі книги
                            bookArrayList.toList()
                        } else {
                            if (searchText.length >= 3) {
                                // фільтровані книги за пошуком
                                bookArrayList.filter {
                                    it.title.toLowerCase(Locale.getDefault()).contains(searchText) ||
                                            it.author.toLowerCase(Locale.getDefault()).contains(searchText)
                                }
                            } else {
                                bookArrayList.toList()
                            }
                        }

                        searchList.clear()
                        searchList.addAll(filteredList)
                        recyclerView.adapter?.notifyDataSetChanged()
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
                    transaction.addToBackStack("$bookInfoFragment")
                    transaction.commit()
                }
            } else {
                progressBar.visibility = View.GONE
                Log.d("SYSTEM_ERROR", "Помилка отримання даних!")
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

                if (::bookAdapter.isInitialized) {
                    // оновити searchList за пошуком
                    val searchText = searchView.query.toString().toLowerCase(Locale.getDefault())
                    val filteredList = if (searchText.isBlank()) {
                        // якщо порожній, то показати всі книги
                        bookArrayList.toList()
                    } else {
                        if (searchText.length >= 3) {
                            // фільтровані книги за пошуком
                            bookArrayList.filter {
                                it.title.toLowerCase(Locale.getDefault()).contains(searchText) ||
                                        it.author.toLowerCase(Locale.getDefault()).contains(searchText)
                            }
                        } else {
                            bookArrayList.toList()
                        }
                    }

                    searchList.clear()
                    searchList.addAll(filteredList)
                    bookAdapter.notifyDataSetChanged()
                } else {
                    recyclerView.adapter = BookAdapter(searchList)
                }

                page++
            } else {
                progressBar.visibility = View.GONE
                Log.d("SYSTEM_ERROR", "Помилка отримання наступних даних!")
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

        requestCall.enqueue(object : Callback<BookApi.BooksResponse> {
            override fun onResponse(call: Call<BookApi.BooksResponse>, response: Response<BookApi.BooksResponse>) {
                if (response.isSuccessful) {
                    val booksResponse = response.body()
                    val books = booksResponse?.results
                    if (!books.isNullOrEmpty()) {
                        callback(books)
                    } else {
                        Toast.makeText(requireContext(), "Список книг пустий!",Toast.LENGTH_SHORT).show()
                        callback(null)
                    }
                } else {
                    if (response.code() == 404) {
                        Log.d("SYSTEM_ERROR","Книги не було знайдено!")
                        callback(null)
                    } else {
                        Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                            .show()
                        callback(null)
                    }
                }
            }
            override fun onFailure(call: Call<BookApi.BooksResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }
}