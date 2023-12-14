package com.example.foxbook.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foxbook.GenreAdapter
import com.example.foxbook.R
import com.example.foxbook.api.Genre


class FiltersFragment : Fragment(R.layout.fragment_filters) {

    private lateinit var spinner: Spinner

    private lateinit var recyclerView: RecyclerView
    private lateinit var genreArrayList: ArrayList<Genre>

    lateinit var genreAdapter: GenreAdapter
    lateinit var genreNames: Array<String>

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

        // Жанри
        genreNames = arrayOf(
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

        // Ініціалізація recyclerView
        recyclerView = view.findViewById(R.id.AllGenresRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        genreArrayList = arrayListOf()
        dataInitialise()

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
        }

        authorForeign.setOnClickListener {
            changeAuthorBackground(authorForeign, authorUkraine)
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
                val selectedItem = parent.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), "Selected $selectedItem",Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

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
    }

    private fun dataInitialise() {
        for (i in genreNames) {
            val genre = Genre(i)
            genreArrayList.add(genre)
        }
        recyclerView.adapter = GenreAdapter(genreArrayList)
    }
}