package com.example.myreviewmanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myreviewmanager.data.remote.model.Movie
import com.example.myreviewmanager.repository.MovieSearchRepository
import kotlinx.coroutines.launch

class MovieSearchViewModel(
    private val repository: MovieSearchRepository
) : ViewModel() {

    // Lista de filmes retornados da busca
    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> = _searchResults

    // Mensagem de erro (caso aconteça)
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Estado de carregamento
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Busca filmes pelo texto digitado
     */
    fun searchMovies(query: String) {
        _isLoading.value = true  // Inicia o carregamento
        viewModelScope.launch {
            try {
                val response = repository.searchMovies(query)

                // Verifica se a API retornou sucesso
                if (response.response == "True") {
                    _searchResults.value = response.results ?: emptyList()
                } else {
                    _errorMessage.value = "Nenhum filme encontrado"
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao buscar filmes: ${e.message}"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false  // Finaliza o carregamento
            }
        }
    }
}
