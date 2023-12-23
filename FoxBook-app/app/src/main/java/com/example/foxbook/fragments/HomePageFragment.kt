package com.example.foxbook.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foxbook.ClientAPI
import com.example.foxbook.R
import com.example.foxbook.adapters.RecommendationAdapter
import com.example.foxbook.api.Book
import com.example.foxbook.api.Recommendations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        recyclerView = view.findViewById(R.id.recyclerRecommendView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = LinearLayoutManager(context,
            RecyclerView.HORIZONTAL, false)

        bookRecommendArrayList = mutableListOf()
        dataInitialise() { recommendations ->
            if (recommendations != null) {
                bookRecommendArrayList.addAll(recommendations)
                bookRecommendAdapter = RecommendationAdapter(bookRecommendArrayList)
                recyclerView.adapter = bookRecommendAdapter


                bookRecommendAdapter.onItemClick = {
                    val bookInfoFragment =
                        BookInfoFragment.newInstance(SearchPageFragment::class.java.simpleName)
                    val bundle = Bundle()
                    bundle.putParcelable("android", it)
                    bookInfoFragment.arguments = bundle

                    val transaction: FragmentTransaction =
                        requireFragmentManager().beginTransaction()
                    transaction.replace(R.id.flFragment, bookInfoFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
            else {
                    Toast.makeText( requireContext(), "Не було отримано рекомендації!!", Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }


    private fun dataInitialise(callback: (List<Book>?) -> Unit){
        val requestCall = ClientAPI.apiService.getRecommendations()

        requestCall.enqueue(object : Callback<Recommendations> {
            override fun onResponse(call: Call<Recommendations>, response: Response<Recommendations>) {
                if (response.isSuccessful) {
                    val booksResponse = response.body()
                    val books = booksResponse?.recommendations
                    Log.d("RECOMMEND", booksResponse.toString())
                    Log.d("RECOMMEND", books.toString())
                    if (!books.isNullOrEmpty()) {
                        Log.d("RECOMMEND", books.toString())
                        callback(books)
                    } else {
                        Toast.makeText(requireContext(), "Даних немає!", Toast.LENGTH_SHORT).show()
                        Log.e("RECOMMEND", "Books list is null")
                        callback(null)
                    }
                } else {
                    if (response.code() == 404) {
                        Log.e("RECOMMEND", "Data not found (404)")
                        callback(null)
                    } else {
                        Log.e("RECOMMEND", "Unsuccessful response: ${response.code()}")
                        Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT)
                            .show()
                        callback(null)
                    }
                }
            }
            override fun onFailure(call: Call<Recommendations>, t: Throwable) {
                Log.e("RECOMMEND", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }

}