package com.example.myreviewmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myreviewmanager.repository.ReviewRepository

@Suppress("UNCHECKED_CAST")
class ReviewViewModelFactory(private val repository: ReviewRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)){
            return ReviewViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknow viewModel class")
    }
}