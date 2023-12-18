package com.example.foxbook.page

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class BookPageAdapter(fragmentManager: FragmentManager, private var pages: Int) : FragmentPagerAdapter(
    fragmentManager
) {
//    init {
//        if (fragmentManager == null) {
//            throw IllegalArgumentException("FragmentManager cannot be null")
//        }
//    }

    override fun getItem(position: Int): Fragment {
        return PageContents.create(position)
    }

    override fun getCount(): Int {
        return pages
    }

    fun incrementPageCount() {
        pages += 1
        notifyDataSetChanged()
    }
}