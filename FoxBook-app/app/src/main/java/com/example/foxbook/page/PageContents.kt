package com.example.foxbook.page

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

open class PageContents : Fragment() {
    companion object {
        const val ARG_PAGE = "page"

        // створення фрагменту, у який додається текст
        fun create(pageNumber: Int): PageContents {
            val fragment = PageFragment()
            val args = Bundle()
            args.putInt(ARG_PAGE, pageNumber)
            fragment.arguments = args
            return fragment
        }
    }

    protected var mPageNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPageNumber = requireArguments().getInt(ARG_PAGE)
    }

    fun getPageNumber(): Int {
        return mPageNumber
    }
}