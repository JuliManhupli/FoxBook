package com.example.foxbook.page

import android.graphics.Typeface
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.foxbook.R
import com.example.foxbook.SharedViewModel
import com.example.foxbook.activities.ReadingActivity


class PageFragment : PageContents() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ініціалізація фрагменту
        val rootView = inflater.inflate(R.layout.page_item, container, false) as ViewGroup
        val contentTextView = rootView.findViewById(R.id.mText) as TextView
        val contents: String = (activity as ReadingActivity?)!!.getContents(mPageNumber)

//        val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
//        val receivedData = sharedViewModel.data.value
//
//        Log.d("AAAAAAAAAAAAAAAAAAAAA", receivedData.toString())

        contentTextView.text = contents
        contentTextView.movementMethod = ScrollingMovementMethod()

        return rootView
    }

}