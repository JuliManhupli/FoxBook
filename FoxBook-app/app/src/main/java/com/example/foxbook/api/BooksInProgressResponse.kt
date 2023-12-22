package com.example.foxbook.api


data class BooksInProgressResponse(val count: Int, val next: String?, val previous: String?, val results: List<BookInProgress>)
