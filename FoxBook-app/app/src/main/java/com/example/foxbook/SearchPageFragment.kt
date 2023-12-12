package com.example.foxbook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foxbook.activities.ValidateEmailActivity
import com.example.foxbook.api.Register
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import kotlin.math.log

class SearchPageFragment : Fragment(R.layout.fragment_search_page) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookArrayList: ArrayList<Book>
    private lateinit var searchView: SearchView
    private lateinit var searchList: ArrayList<Book>
    private lateinit var bookAdapter: BookAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.search_recycler_view)
        searchView = view.findViewById(R.id.searchUpperBar)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        bookArrayList = arrayListOf()
        searchList = arrayListOf()

        loadData()
    }

    private fun loadData() {
        getAllBooks { booksFromApi ->
            if (booksFromApi != null) {
                bookArrayList.addAll(booksFromApi)

                Log.d("qwe", bookArrayList.toString())
                Log.d("qwe", "bookArrayList")
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
                Toast.makeText(
                    requireContext(),
                    "Не вдалося отримати дані з бази даних!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getAllBooks(callback: (List<Book>?) -> Unit) {
        val requestCall = ClientAPI.apiService.getBooks()

        requestCall.enqueue(object : Callback<List<Book>> {
            override fun onResponse(call: Call<List<Book>>, response: Response<List<Book>>) {
                if (response.isSuccessful) {
                    val books = response.body()
                    Log.d("qwe", books.toString())
                    Log.d("qwe", "books.toString()")
                    callback(books)
                } else {
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                Log.d("qwe", t.toString())
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }
}
