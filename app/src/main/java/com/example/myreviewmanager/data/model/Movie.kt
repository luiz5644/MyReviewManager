package com.example.myreviewmanager.data.remote.model

import com.google.gson.annotations.SerializedName

data class Movie(

    @SerializedName("imdbID")
    val imdbID: String,
    @SerializedName("Title")
    val title: String,
    @SerializedName("Poster")
    val posterPath: String?,
    @SerializedName("Year")
    val releaseDate: String?,

    val overview: String = ""
)