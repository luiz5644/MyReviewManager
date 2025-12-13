package com.example.myreviewmanager.repository

import com.example.myreviewmanager.data.remote.RetrofitClient
import com.example.myreviewmanager.data.remote.model.MovieResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieSearchRepository {

    private val apiService = RetrofitClient.omdBApiService

    suspend fun searchMovies(query: String): MovieResponse {
        return withContext(Dispatchers.IO) {
            apiService.searchMovies(query)
        }
    }
}
