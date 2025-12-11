package com.example.myreviewmanager.data.remote

import com.example.myreviewmanager.data.remote.model.MovieResponse // Usaremos a classe MovieResponse existente

import retrofit2.http.GET
import retrofit2.http.Query


interface OmdBApi {

    companion object {

        const val API_KEY = "470853d3"
    }


    @GET("/")
    suspend fun searchMovies(
        @Query("s") query: String,
        @Query("apikey") apiKey: String = API_KEY,
        @Query("type") type: String = "movie"
    ): MovieResponse
}