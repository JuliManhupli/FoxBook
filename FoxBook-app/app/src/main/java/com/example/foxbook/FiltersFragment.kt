package com.example.foxbook

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton

class FiltersFragment : Fragment(R.layout.fragment_filters) {

    private val genreButtons = ArrayList<Button>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        loopThroughButtons(view.findViewById(R.id.GenresGroup))

    }

    private fun loopThroughButtons( parent: ViewGroup){
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)

            if (child is Button) {
                genreButtons.add(child)
            }
        }
    }
}