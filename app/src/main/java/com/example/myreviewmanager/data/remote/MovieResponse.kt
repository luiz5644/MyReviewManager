package com.example.myreviewmanager.data.remote.model

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    // A OMDb retorna 'Search'
    @SerializedName("Search")
    val results: List<Movie>?,
    @SerializedName("totalResults")
    val totalResults: String?,
    @SerializedName("Response") // Campo para indicar sucesso/falha
    val response: String
)