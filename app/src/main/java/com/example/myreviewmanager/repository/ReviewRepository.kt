package com.example.myreviewmanager.repository

import androidx.lifecycle.LiveData
import com.example.myreviewmanager.data.Review
import com.example.myreviewmanager.data.ReviewDao

class ReviewRepository(private val reviewDao: ReviewDao) {
    val allReviews: LiveData<List<Review>> = reviewDao.getAllReviews()

    suspend fun insertReview(review: Review): Long {
       return reviewDao.insertReview(review)
    }

    suspend fun updateReview(review: Review){
        reviewDao.updateReview(review)
    }

    suspend fun deleteReview(review: Review){
        reviewDao.deleteReview(review)
    }

    suspend fun getReviewById(reviewId: Long): Review {
       return reviewDao.getReviewById(reviewId)
    }

}