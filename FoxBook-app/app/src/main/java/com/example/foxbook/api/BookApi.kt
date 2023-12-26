package com.example.foxbook.api

class BookApi () {
    data class BookPage(val text: String)

    class ReadingProgress(val reading_progress: Int)

    data class BookTextChunks(val text_chunks: List<String>)

    data class Recommendations(val recommendations: List<Book>)

    data class BookToRead(val book_to_read: List<BookInProgress>)

    data class BooksResponse(val count: Int, val next: String?, val previous: String?, val results: List<Book>)

    data class BooksInProgressResponse(val count: Int, val next: String?, val previous: String?, val results: List<BookInProgress>)

}

