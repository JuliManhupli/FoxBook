package com.example.foxbook.page

import android.os.AsyncTask
import android.text.TextPaint
import android.util.Log
import com.example.foxbook.activities.ReadingActivity


class PagerTask(private val activity: ReadingActivity) :
    AsyncTask<ReadingActivity.ViewAndPaint, ReadingActivity.ProgressTracker, Void>() {

    override fun onProgressUpdate(vararg values: ReadingActivity.ProgressTracker) {
        activity.onPageProcessedUpdate(values[0])
    }

    override fun doInBackground(vararg params: ReadingActivity.ViewAndPaint?): Void? {

        val viewAndPaint: ReadingActivity.ViewAndPaint? = params[0]
        val progress = ReadingActivity.ProgressTracker()
        val paint: TextPaint = viewAndPaint!!.paint

        var charactersProcessed = 0
        var lineCount = 0

        val maxLineCount: Int = viewAndPaint.maxLineCount
        var totalCharactersProcessedSoFar = 0

        // contentString is the whole string of the book
        var totalPages = 0
        while (viewAndPaint.contentString.isNotEmpty()) {
            while (lineCount < maxLineCount && charactersProcessed < viewAndPaint.contentString.length) {
                val breakResult = paint.breakText(
                    viewAndPaint.contentString.substring(charactersProcessed),
                    true,
                    viewAndPaint.screenWidth.toFloat(),
                    null
                )
                // Check for newline characters within the breakResult
                val newlineIndex = viewAndPaint.contentString.indexOf('\n', charactersProcessed)
                val tabIndex = viewAndPaint.contentString.indexOf('\t', charactersProcessed)

                if ((newlineIndex != -1 && newlineIndex < charactersProcessed + breakResult) ||
                    (tabIndex != -1 && tabIndex < charactersProcessed + breakResult)) {
                    // Include characters up to the newline or tab character
                    val nextCharacterIndex = minOf(
                        if (newlineIndex != -1) newlineIndex else Int.MAX_VALUE,
                        if (tabIndex != -1) tabIndex else Int.MAX_VALUE
                    )
                    charactersProcessed = nextCharacterIndex + 1
                } else {
                    // Include the entire breakResult
                    charactersProcessed += breakResult
                }
                Log.d("HEEEEEEEEEEEEEEEE", charactersProcessed.toString())
                lineCount++
            }

            // Retrieve the String to be displayed in the current textview
            var stringToBeDisplayed: String = viewAndPaint.contentString.substring(0, charactersProcessed)
            val nextIndex = charactersProcessed
            val nextChar =
                if (nextIndex < viewAndPaint.contentString.length) viewAndPaint.contentString[nextIndex] else ' '
            if (!Character.isWhitespace(nextChar)) {
                stringToBeDisplayed =
                    stringToBeDisplayed.substring(0, stringToBeDisplayed.lastIndexOf(" "))
            }
            charactersProcessed = stringToBeDisplayed.length
            viewAndPaint.contentString = viewAndPaint.contentString.substring(charactersProcessed)

            // publish progress
            progress.totalPages = totalPages
            progress.addPage(
                totalPages,
                totalCharactersProcessedSoFar,
                totalCharactersProcessedSoFar + charactersProcessed
            )
            publishProgress(progress)

            totalCharactersProcessedSoFar += charactersProcessed

            // reset per page items
            charactersProcessed = 0
            lineCount = 0

            // збільшуємо кількість сторінок
            totalPages++
        }
        return null
    }
}