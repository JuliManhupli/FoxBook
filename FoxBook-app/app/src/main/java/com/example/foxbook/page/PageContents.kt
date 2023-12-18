package com.example.foxbook.page

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

open class PageContents : Fragment() {
    companion object {
        const val ARG_PAGE = "page"

        fun create(pageNumber: Int): PageContents {
            val fragment = PageFragment()
            Log.d("PAGER", fragment.toString())
            val args = Bundle()
            Log.d("PAGER", args.toString())
            args.putInt(ARG_PAGE, pageNumber)
            fragment.arguments = args
            Log.d("PAGER", args.toString())
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