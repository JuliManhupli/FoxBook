package com.example.foxbook.activities

import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.foxbook.page.BookPageAdapter
import com.example.foxbook.page.PagerTask
import com.example.foxbook.R
import kotlin.math.abs


open class ReadingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var bookPageAdapter: FragmentPagerAdapter
    private var mPages: Map<String, String> = HashMap()
    private lateinit var pageIndicator: LinearLayout
    private lateinit var progressBar: ProgressBar
    private var bookString = ""
    private lateinit var mDisplay: Display

    class ViewAndPaint(
        var paint: TextPaint,
        var textViewLayout: ViewGroup,
        var screenWidth: Int,
        var maxLineCount: Int,
        var contentString: String
    )

    class ProgressTracker {

        var totalPages = 0
        var pages: MutableMap<String, String> = HashMap()

        fun addPage(page: Int, startIndex: Int, endIndex: Int) {
            val thePage = page.toString()
            val indexMarker = "$startIndex,$endIndex"
            pages[thePage] = indexMarker
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading)

        bookString = "Contrary to popular belief, Lorem Ipsum\n \n\nis not simply random text. It has roots in" +
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
//                "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in" +
//                "a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
//                "McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more" +
//                "obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
//                "the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from" +
//                "sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
//                "by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular" +
//                "during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
//                "a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
//                "below for those interested. Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
//                "by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
//                "the 1914 translation by H. Rackham." +
//                "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in" +
//                "a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
//                "McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more" +
//                "obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
//                "the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from" +
//                "sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
//                "by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular" +
//                "during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
//                "a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
//                "below for those interested. Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
//                "by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
//                "the 1914 translation by H. Rackham." +
//                "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in" +
//                "a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
//                "McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more" +
//                "obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
//                "the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from" +
//                "sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
//                "by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular" +
//                "during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
//                "a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
//                "below for those interested. Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
//                "by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
//                "the 1914 translation by H. Rackham." +
//                "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in" +
//                "a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
//                "McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more" +
//                "obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
//                "the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from" +
//                "sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
//                "by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular" +
//                "during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
//                "a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
//                "below for those interested. Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
//                "by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
//                "the 1914 translation by H. Rackham." +
//                "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in" +
//                "a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
//                "McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more" +
//                "obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
//                "the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from" +
//                "sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
//                "by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular" +
//                "during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
//                "a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
//                "below for those interested. Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
//                "by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
//                "the 1914 translation by H. Rackham." +
//                "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in" +
//                "a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
//                "McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more" +
//                "obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
//                "the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from" +
//                "sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
//                "by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular" +
//                "during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
//                "a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
//                "below for those interested. Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
//                "by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
//                "the 1914 translation by H. Rackham." +
//                "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in" +
//                "a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
//                "McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more" +
//                "obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
//                "the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from" +
//                "sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
//                "by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular" +
//                "during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
//                "a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
//                "below for those interested. Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
//                "by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
//                "the 1914 translation by H. Rackham." +
//                "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in" +
//                "a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
//                "McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more" +
//                "obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
//                "the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from" +
//                "sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
//                "by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular" +
//                "during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
//                "a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
//                "below for those interested. Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
//                "by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
//                "the 1914 translation by H. Rackham." +
//                "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in" +
//                "a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard" +
//                "McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more" +
//                "obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of" +
//                "the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from" +
//                "sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum(The Extremes of Good and Evil)" +
//                "by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular" +
//                "during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet, comes from" +
//                "a line in section 1.10.32.The standard chunk of Lorem Ipsum used since the 1500s is reproduced" +
//                "below for those interested. Sections 1.10.32 and 1.10.33 from de Finibus Bonorum et Malorum" +
//                "by Cicero are also reproduced in their exact original form, accompanied by English versions from" +
//                "the 1914 translation by H. Rackham."



        progressBar = findViewById(R.id.progressBarInReading)

        val textViewLayout = layoutInflater.inflate(
            R.layout.page_item,
            window.decorView.findViewById<View>(android.R.id.content) as ViewGroup,
            false
        ) as ViewGroup



        val contentTextView = textViewLayout.findViewById(R.id.mText) as TextView

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            contentTextView.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
//        }
//        contentTextView.text = bookString
//
////        contentTextView.justificationMode = View.TEXT_ALIGNMENT_GRAVITY
//        Log.d("PAGER", contentTextView.text as String)

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = findViewById(R.id.viewPagerForReading)


        // обчислюємо розміри екрану
        mDisplay = windowManager.defaultDisplay
        val viewAndPaint= ViewAndPaint(
            contentTextView.paint, textViewLayout,
            screenWidth(), getMaxLineCount(contentTextView), bookString
        )
        val pt = PagerTask(this)
        pt.execute(viewAndPaint)
    }

    private fun screenWidth(): Int {
        val horizontalMargin = resources.getDimension(R.dimen.activity_horizontal_margin) * 2
        return (mDisplay.width - horizontalMargin).toInt()
    }

    private fun getMaxLineCount(view: TextView): Int {
        val verticalMargin = resources.getDimension(R.dimen.activity_vertical_margin) * 3
        val screenHeight = mDisplay.height
        val paint = view.paint

        //Working Out How Many Lines Can Be Entered In The Screen
        val fm = paint.fontMetrics
        var textHeight = fm.top - fm.bottom
        textHeight = abs(textHeight)
        var maxLineCount = ((screenHeight - verticalMargin) / textHeight).toInt()

        // add extra spaces at the bottom, remove 2 lines
        maxLineCount -= 2
        return maxLineCount
    }

    private fun initViewPager() {
        bookPageAdapter = BookPageAdapter(supportFragmentManager, 1)
        viewPager.adapter = bookPageAdapter
        viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                showPageIndicator(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    fun onPageProcessedUpdate(progress: ProgressTracker) {
        mPages = progress.pages
        // init the pager if necessary
        if (!::bookPageAdapter.isInitialized) {
            Log.d("PAGER", "YES")
            initViewPager()
            progressBar.visibility = View.GONE
        } else {
            Log.d("PAGER", "NO :(")
            (bookPageAdapter as BookPageAdapter).incrementPageCount()
        }
        addPageIndicator(progress.totalPages)
    }

    private fun addPageIndicator(pageNumber: Int) {
        pageIndicator = findViewById(R.id.pageIndicator)

        val view = View(this)

        val params: ViewGroup.LayoutParams = TableLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            1f
        )

        view.layoutParams = params

        if (pageNumber == 0) {
            view.setBackgroundResource(R.drawable.current_page_indicator)
        } else {
            view.setBackgroundResource(R.drawable.indicator_bg)
        }

        view.tag = pageNumber
        pageIndicator.addView(view)
    }

    private fun showPageIndicator(position: Int) {
        try {
            pageIndicator = findViewById(R.id.pageIndicator)
            val selectedIndexIndicator = pageIndicator.getChildAt(position)

            // зафарбувати нинішню сторінку
            selectedIndexIndicator.setBackgroundResource(R.drawable.current_page_indicator)

            // іншу - без кольору
            if (position > 0) {
                val leftView = pageIndicator.getChildAt(position - 1)
                leftView.setBackgroundResource(R.drawable.indicator_bg)
            }
            if (position < mPages.size) {
                val rightView = pageIndicator.getChildAt(position + 1)
                rightView.setBackgroundResource(R.drawable.indicator_bg)
            }
        } catch (e: Exception) {
            Log.e("MainActivityException", e.toString())
        }
    }

    fun getContents(pageNumber: Int): String {

        val page = pageNumber.toString()
        val textBoundaries = mPages[page]

        if (textBoundaries != null) {
            val bounds = textBoundaries.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val startIndex = bounds[0].toInt()
            val endIndex = bounds[1].toInt()
            return bookString.substring(startIndex, endIndex).trim { it <= ' ' }
        }
        return ""
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }


//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        return if (id == R.id.action_settings) {
//            true
//        } else super.onOptionsItemSelected(item)
//    }
}