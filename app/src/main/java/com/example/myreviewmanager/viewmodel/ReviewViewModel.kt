package com.example.myreviewmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myreviewmanager.data.Review
import com.example.myreviewmanager.repository.ReviewRepository
import com.example.myreviewmanager.data.UserManager
import kotlinx.coroutines.launch

class ReviewViewModel(private val repository: ReviewRepository): ViewModel() {

    // CORRIGIDO: O LiveData agora é inicializado buscando APENAS as reviews do usuário logado.
    val allReviews = repository.getAllReviews(UserManager.requireUserId)

    fun insertReview(review: Review) = viewModelScope.launch {
        repository.insertReview(review)
    }

    fun updateReview(review: Review) = viewModelScope.launch {
        repository.updateReview(review)
    }

    fun deleteReview(review: Review) = viewModelScope.launch {
        repository.deleteReview(review)
    }

    fun getReviewById(reviewId: Long, onResult: (Review)->Unit) = viewModelScope.launch {
        val review = repository.getReviewById(reviewId)
        onResult(review)
    }

    // =================================================================
    // NOVO: FUNÇÃO PARA CHECAR DUPLICIDADE
    // =================================================================
    suspend fun checkExistingReview(tmdbId: String, currentUserId: Long): Review? {
        return repository.getReviewByTmdbIdAndUserId(tmdbId, currentUserId)
    }

}