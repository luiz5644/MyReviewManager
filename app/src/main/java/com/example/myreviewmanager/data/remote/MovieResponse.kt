package com.example.myreviewmanager.data.remote.model

import com.google.gson.annotations.SerializedName

data class MovieResponse(

    // Lista de filmes retornada pela OMDb
    @SerializedName("Search")
    val results: List<Movie>?,

    @SerializedName("totalResults")
    val totalResults: String?,

    // "True" ou "False"
    @SerializedName("Response")
    val response: String
)
