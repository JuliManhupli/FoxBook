package com.example.foxbook.api

import android.os.Parcel
import android.os.Parcelable

data class Genre(val genre: String, var isSelected: Boolean = false): Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()!!) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(genre)
    }

    companion object CREATOR : Parcelable.Creator<Genre> {
        override fun createFromParcel(parcel: Parcel): Genre {
            return Genre(parcel)
        }

        override fun newArray(size: Int): Array<Genre?> {
            return arrayOfNulls(size)
        }
    }

}
