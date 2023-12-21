package com.example.foxbook.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foxbook.page.BookPageAdapter
import com.example.foxbook.R
import com.example.foxbook.api.BookPage


open class ReadingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pageArrayList: ArrayList<BookPage>

    lateinit var pageAdapter: BookPageAdapter

    private val processedItems = mutableSetOf<Int>()

    private lateinit var pageText: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading)

        val textLayout = layoutInflater.inflate(R.layout.page_item, null)
        val textView: TextView = textLayout.findViewById(R.id.mText)

        Log.d("RRRRRRRRRRRRRRRRRRR", textView.text.toString())
        Log.d("RRRRRRRRRRRRRRRRRRR", textView.textSize.toString())


        pageText = arrayListOf(
            "Contrary to popular belief, Lorem Ipsum\n \n\nis not simply random text. It has roots in" +
                    " a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
                    " McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more" +
                    " obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
                    " the word in classical literature, discovered the undoubtable source.\n Lorem Ipsum comes from" +
                    " sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
                    " by Cicero, written in 45 BC.\n This book is a treatise on the theory of ethics, very popular" +
                    " during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
                    " a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
                    "below for those interested.\n\n\n Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
                    " by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
                    " the 1914 translation by H. Rackham." +
                    " Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in" +
                    " a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
                    "McClintock, a Latin professor at Hampden-Sydney College in Virginia,\n\nlooked up one of the more" +
                    "obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
                    "the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from" +
                    "sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
                    "by Cicero, written in 45 BC.\n This book is a treatise on the theory of ethics, very popular" +
                    "during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
                    "a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
                    "below for those interested.\n Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
                    "by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
                    "the 1914 translation by H. Rackham." +
                    "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in" +
                    "a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
                    "McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more" +
                    "obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
                    "the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from" +
                    "sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
                    "by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular" +
                    "during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
                    "a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
                    "below for those interested. Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
                    "by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
                    "the 1914 translation by H. Rackham." +
                    "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in" +
                    "a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
                    "McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more" +
                    "obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
                    "the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from" +
                    "sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
                    "by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular" +
                    "during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
                    "a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
                    "below for those interested. Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
                    "by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
                    "the 1914 translation by H. Rackham.",
            "Contrary to popular belief, Lorem Ipsum\n \n\nis not simply random text. It has roots in" +
                    " a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
                    " McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more" +
                    " obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
                    " the word in classical literature, discovered the undoubtable source.\n Lorem Ipsum comes from" +
                    " sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
                    " by Cicero, written in 45 BC.\n This book is a treatise on the theory of ethics, very popular" +
                    " during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
                    " a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
                    "below for those interested.\n\n\n Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
                    " by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
                    " the 1914 translation by H. Rackham." +
                    " Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in" +
                    " a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
                    "McClintock, a Latin professor at Hampden-Sydney College in Virginia,\n\nlooked up one of the more" +
                    "obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
                    "the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from" +
                    "sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
                    "by Cicero, written in 45 BC.\n This book is a treatise on the theory of ethics, very popular" +
                    "during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
                    "a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
                    "below for those interested.\n Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
                    "by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
                    "the 1914 translation by H. Rackham." +
                    "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in" +
                    "a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
                    "McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more" +
                    "obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
                    "the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from" +
                    "sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
                    "by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular" +
                    "during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
                    "a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
                    "below for those interested. Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
                    "by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
                    "the 1914 translation by H. Rackham." +
                    "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in" +
                    "a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
                    "McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more" +
                    "obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
                    "the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from" +
                    "sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
                    "by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular" +
                    "during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
                    "a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
                    "below for those interested. Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
                    "by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
                    "the 1914 translation by H. Rackham."
        )

        recyclerView = findViewById(R.id.recyclerPageViewReading)
        val layoutManager = LinearLayoutManager(this)

        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        pageArrayList = arrayListOf()

        loadData()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()


                for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
                    if (!processedItems.contains(i)) {
                        Log.d("RecyclerViewScroll", "Last Visible Item: $lastVisibleItemPosition")
                        // Add the item to the set of processed items to avoid duplicates
                        processedItems.add(i)
                    }
                }
            }


        })
    }

    private fun loadData() {
        for (i in pageText){
            val bookPageText = BookPage(i)
            pageArrayList.add(bookPageText)
        }
        recyclerView.adapter = BookPageAdapter(pageArrayList)
    }

}