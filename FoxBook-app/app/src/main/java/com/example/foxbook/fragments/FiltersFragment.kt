package com.example.foxbook.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.example.foxbook.adapters.GenreAdapter
import com.example.foxbook.R
import com.example.foxbook.api.Genre
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FiltersFragment : Fragment(R.layout.fragment_filters) {

    private lateinit var spinner: Spinner

    private lateinit var recyclerView: RecyclerView
    private lateinit var genreArrayList: ArrayList<Genre>

    private lateinit var genreAdapter: GenreAdapter

    private var selectedGenres: List<String> = emptyList()
    private var selectedAuthors: String? = null
    private var selectedSorting: String? = null

    private var targetFragment: String = ""

    companion object {
        fun newInstance(targetFragment: String): FiltersFragment {
            val fragment = FiltersFragment()
            fragment.targetFragment = targetFragment
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Назад до пошуку
        val backToSearch: ImageButton = view.findViewById(R.id.imgBtnBackToSearchFromFilters)

        backToSearch.setOnClickListener {

            selectedGenres = emptyList()
            selectedAuthors = null
            selectedSorting = spinner.getItemAtPosition(0).toString()

            when (targetFragment) {
                SearchPageFragment::class.java.simpleName -> {
                    navigateToSearchPageFragment()
                }
                FavouriteBooksFragment::class.java.simpleName -> {
                    navigateToFavoriteBooksFragment()
                }
                ReadingInProgressFragment::class.java.simpleName -> {
                    navigateToReadingInProgressFragment()
                }
            }
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
            selectedAuthors = if (selectedAuthors == "Українська література") {
                null
            } else {
                "Українська література"
            }

        }

        authorForeign.setOnClickListener {
            changeAuthorBackground(authorForeign, authorUkraine)
            selectedAuthors = if (selectedAuthors == "Зарубіжна література") {
                null
            } else {
                "Зарубіжна література"
            }
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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        // Кнопка пошуку
        val searchByFilters: Button = view.findViewById(R.id.btnSearchByFilters)
        searchByFilters.setOnClickListener {

            when (targetFragment) {
                SearchPageFragment::class.java.simpleName -> {
                    navigateToSearchPageFragment()
                }
                FavouriteBooksFragment::class.java.simpleName -> {
                    navigateToFavoriteBooksFragment()
                }
                ReadingInProgressFragment::class.java.simpleName -> {
                    navigateToReadingInProgressFragment()
                }
            }

        }

        // Кнопка очищення фільтрів
        val clearButton: ImageButton = view.findViewById(R.id.imgBtnClearFilters)
        clearButton.setOnClickListener {

            for (genre in genreArrayList) {
                genre.isSelected = false
            }

            genreAdapter.notifyDataSetChanged()

            // змінюємо кнопки авторів у початковий стан
            authorUkraine.setBackgroundResource(R.color.white)
            authorUkraine.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

            authorForeign.setBackgroundResource(R.color.white)
            authorForeign.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

            // Вертаємо сортування на варіант без нього
            spinner.setSelection(0)

            selectedGenres = emptyList()
            selectedAuthors = null
            selectedSorting = spinner.getItemAtPosition(0).toString()
        }
    }

    private fun navigateToSearchPageFragment() {
        val searchPageFragment = SearchPageFragment().apply {
            arguments = createBundle()
        }
        navigateToFragment(searchPageFragment)
    }

    private fun navigateToFavoriteBooksFragment() {
        val favoriteBooksFragment = FavouriteBooksFragment().apply {
            arguments = createBundle()
        }
        navigateToFragment(favoriteBooksFragment)
    }

    private fun navigateToReadingInProgressFragment() {
        val readingInProgressFragment = ReadingInProgressFragment().apply {
            arguments = createBundle()
        }
        navigateToFragment(readingInProgressFragment)
    }

    private fun createBundle(): Bundle {
        return Bundle().apply {
            putStringArrayList("selectedGenres", ArrayList(selectedGenres))
            putString("selectedAuthors", selectedAuthors)
            putString("selectedSorting", selectedSorting)
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.flFragment, fragment)
        transaction.addToBackStack("$fragment")
        transaction.commit()
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
        val genreName = button.text.toString()

        if ((button.background as ColorDrawable).color == standartStateColor) {
            selectedGenres += genreName
        }
        else if ((button.background as ColorDrawable).color == chosenStateColor) {
            selectedGenres -= genreName
        }

        for (genre in genreArrayList) {
            genre.isSelected = selectedGenres.contains(genre.genre)
        }

        genreAdapter.notifyDataSetChanged()
    }

    private fun getAllGenre() {
        val requestCall = ClientAPI.apiService.getGenres()

        requestCall.enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val genres = response.body()
                    if (genres != null) {
                        updateUIWithGenres(genres)
                    } else {
                        Toast.makeText(requireContext(), "Список жанрів пустий!",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Не отримано дані!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(requireContext(), "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUIWithGenres(genres: List<String>) {
        genreArrayList.clear()
        for (genreName in genres) {
            val genre = Genre(genreName)
            genreArrayList.add(genre)
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }
}