package com.example.foxbook.api

import android.os.Parcel
import android.os.Parcelable


data class BookInProgress(

    val id: Int,
    val cover: String,
    val title: String,
    val author: String,
    val rating: Double,
    val genre: String,
    val annotation: String,
    val pages: Int,
    val reading_progress: Int): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(cover)
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeDouble(rating)
        parcel.writeString(genre)
        parcel.writeString(annotation)
        parcel.writeInt(pages)
        parcel.writeInt(reading_progress)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookInProgress> {
        override fun createFromParcel(parcel: Parcel): BookInProgress {
            return BookInProgress(parcel)
        }

        override fun newArray(size: Int): Array<BookInProgress?> {
            return arrayOfNulls(size)
        }
    }
}
