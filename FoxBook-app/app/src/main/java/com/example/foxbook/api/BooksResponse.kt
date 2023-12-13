package com.example.foxbook.api

import com.example.foxbook.Book

data class BooksResponse(val count: Int, val next: String?, val previous: String?, val results: List<Book>)
