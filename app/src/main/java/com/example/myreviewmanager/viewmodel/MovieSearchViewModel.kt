package com.example.myreviewmanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myreviewmanager.data.remote.model.Movie
import com.example.myreviewmanager.repository.MovieSearchRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar a busca de filmes e o estado da Nova Tela.
 */
class MovieSearchViewModel(private val repository: MovieSearchRepository) : ViewModel() {

    // LiveData que armazena a lista de filmes encontrados
    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> = _searchResults

    // LiveData que rastreia o estado de carregamento (mostra/esconde a barra de progresso)
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para mensagens de erro
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Inicia a busca de filmes na API.
     * @param query O termo de busca.
     */
    fun searchMovies(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = repository.searchMovies(query)
            _isLoading.value = false

            result.onSuccess { response ->
                // Atualiza o LiveData com a lista de filmes
                _searchResults.value = response.results
            }.onFailure { error ->
                // Define uma mensagem de erro
                _errorMessage.value = "Erro ao buscar filmes: ${error.message}"
                _searchResults.value = emptyList()
            }
        }
    }
}

/**
 * Factory personalizada para criar o MovieSearchViewModel.
 */
class MovieSearchViewModelFactory(private val repository: MovieSearchRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieSearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieSearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}