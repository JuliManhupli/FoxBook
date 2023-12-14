package com.example.foxbook.api

import android.os.Parcel
import android.os.Parcelable

data class Book(
    val cover: String,
    val title: String,
    val author: String,
    val rating: Double,
    val genre: String,
    val annotation: String): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(cover)
        dest.writeString(title)
        dest.writeString(author)
        dest.writeDouble(rating)
        dest.writeString(genre)
        dest.writeString(annotation)
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}
