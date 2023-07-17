package com.example.flixt.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    val posterPath: String,
    val overview: String,
) : Parcelable
