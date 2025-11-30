package com.example.myreviewmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myreviewmanager.data.Review
import com.example.myreviewmanager.repository.ReviewRepository
import kotlinx.coroutines.launch

class ReviewViewModel(private val repository: ReviewRepository): ViewModel() {

    val allReviews = repository.allReviews

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

}