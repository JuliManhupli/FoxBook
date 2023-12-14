package com.example.foxbook.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foxbook.ClientAPI
import com.example.foxbook.GenreAdapter
import com.example.foxbook.R
import com.example.foxbook.api.Book
import com.example.foxbook.api.BooksResponse
import com.example.foxbook.api.Genre
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class FiltersFragment : Fragment(R.layout.fragment_filters) {

    private lateinit var spinner: Spinner

    private lateinit var recyclerView: RecyclerView
    private lateinit var genreArrayList: ArrayList<Genre>

    lateinit var genreAdapter: GenreAdapter

    private var selectedGenres: List<String> = emptyList()
    private var selectedAuthors: String? = null
    private var selectedSorting: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Назад до пошуку
        val backToSearch: ImageButton = view.findViewById(R.id.imgBtnBackToSearchFromFilters)

        backToSearch.setOnClickListener{
            val bookInfoFragment = SearchPageFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.flFragment, bookInfoFragment)
            transaction.commit()
        }

        // Ініціалізація recyclerView
        recyclerView = view.findViewById(R.id.AllGenresRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        genreArrayList = arrayListOf()
        getAllGenre()

        genreAdapter = GenreAdapter(genreArrayList)
        recyclerView.adapter = genreAdapter

        genreAdapter.onItemClick = {_, button ->
            changeGenreBackground(button)
        }

        // Кнопки авторів
        val authorUkraine: AppCompatButton = view.findViewById(R.id.btnAuthorUkraine)
        val authorForeign: AppCompatButton = view.findViewById(R.id.btnAuthorForeign)

        authorUkraine.setOnClickListener {
            changeAuthorBackground(authorUkraine, authorForeign)
            selectedAuthors = "Українська література"
        }

        authorForeign.setOnClickListener {
            changeAuthorBackground(authorForeign, authorUkraine)
            selectedAuthors = "Зарубіжна література"
        }

        // Сортування
        spinner = view.findViewById(R.id.spnrSorting)

        val listOfSortings = listOf(
            "Без сортувань",
            "Назва(А-Я)",
            "Назва(Я-А)",
            "Автор(А-Я)",
            "Автор(Я-А)",
            "Оцінка(За зростанням)",
            "Оцінка(За спаданням)")

        // Адаптер для сортувань
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOfSortings)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        // При виборі елементу
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedSorting = parent.getItemAtPosition(position).toString()
                val selectedItem = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO()


            }

        }


        // Кнопка пошуку
        val searchByFilters: Button = view.findViewById(R.id.btnSearchByFilters)
        searchByFilters.setOnClickListener {
            Log.d("qwe", "11")
            Log.d("qwe", selectedGenres.toString())
            Log.d("qwe", selectedAuthors ?: "No Sorting Selected")
            Log.d("qwe", selectedSorting ?: "No Sorting Selected")

            // Передача параметрів фільтра як аргументів
            val searchPageFragment = SearchPageFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList("selectedGenres", ArrayList(selectedGenres))
                    putString("selectedAuthors", selectedAuthors)
                    putString("selectedSorting", selectedSorting)
                }
            }

            // Назад до SearchPageFragment
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.flFragment, searchPageFragment)
            transaction.commit()
        }

        // Кнопка очищення фільтрів
        val clearButton: ImageButton = view.findViewById(R.id.imgBtnClearFilters)
        clearButton.setOnClickListener {

            // змінюємо всі жанри у невибірковий стан
            for (i in 0 until recyclerView.childCount) {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(i)
                if (viewHolder is GenreAdapter.GenreViewHolder) {
                    val button = viewHolder.filterBtn

                    button.setBackgroundResource(R.color.white)
                    button.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }

            // змінюємо кнопки авторів у початковий стан
            authorUkraine.setBackgroundResource(R.color.white)
            authorUkraine.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

            authorForeign.setBackgroundResource(R.color.white)
            authorForeign.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

            // Вертаємо сортування на варіант без нього
            spinner.setSelection(0)
        }
    }

    private fun changeAuthorBackground(buttonOn: AppCompatButton, buttonOff: AppCompatButton) {

        val standartStateColor = ContextCompat.getColor(requireContext(), R.color.white)
        val chosenStateColor = ContextCompat.getColor(requireContext(), R.color.chosen_filter)

        if ((buttonOn.background as ColorDrawable).color == standartStateColor) {
            buttonOn.setBackgroundResource(R.color.chosen_filter)
            buttonOn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            buttonOff.setBackgroundResource(R.color.white)
            buttonOff.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
        else if ((buttonOn.background as ColorDrawable).color == chosenStateColor) {
            buttonOn.setBackgroundResource(R.color.white)
            buttonOn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
    }


    private fun changeGenreBackground(button: AppCompatButton) {

        val standartStateColor = ContextCompat.getColor(requireContext(), R.color.white)
        val chosenStateColor = ContextCompat.getColor(requireContext(), R.color.chosen_filter)

        if ((button.background as ColorDrawable).color == standartStateColor) {
            button.setBackgroundResource(R.color.chosen_filter)
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        else if ((button.background as ColorDrawable).color == chosenStateColor) {
            button.setBackgroundResource(R.color.white)
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

        val genreName = button.text.toString()
        selectedGenres = if (selectedGenres.contains(genreName)) {
            selectedGenres - genreName
        } else {
            selectedGenres + genreName
        }

        // Print or use the selected genres as needed
        Log.d("SelectedGenres", selectedGenres.toString())

    }

    private fun getAllGenre() {
        val requestCall = ClientAPI.apiService.getGenres()

        requestCall.enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val genres = response.body()
                    if (genres != null) {
                        Log.d("qwe", genres.toString())
                        updateUIWithGenres(genres)
                    } else {
                        Log.e("qwe", "Genres list is null")
                        // Handle the case where genres list is null
                    }
                } else {
                    // Handle unsuccessful response
                    Log.e("qwe", "Unsuccessful response: ${response.code()}")
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT).show()
                    // Handle the case where the response is not successful
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.e("qwe", "API request failed with exception", t)
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
                // Handle the case where the API request fails
            }
        })
    }

    private fun updateUIWithGenres(genres: List<String>) {
        // Update your UI with the fetched genres
        // Assuming you have a recycler view adapter (genreAdapter), update the data and notify the adapter
        genreArrayList.clear()
        for (genreName in genres) {
            val genre = Genre(genreName)
            genreArrayList.add(genre)
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }
}