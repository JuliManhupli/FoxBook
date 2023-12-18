package com.example.foxbook.page

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.foxbook.R
import com.example.foxbook.activities.ReadingActivity


class PageFragment : PageContents() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.page_item, container, false) as ViewGroup
        Log.d("PAGER", "root $rootView")
        val contentTextView = rootView.findViewById(R.id.mText) as TextView
        val contents: String = (activity as ReadingActivity?)!!.getContents(mPageNumber)
        Log.d("PAGER", "contents $contents")

        contentTextView.text = contents
        contentTextView.movementMethod = ScrollingMovementMethod()

        return rootView
    }

}