package com.example.myreviewmanager.repository

import androidx.lifecycle.LiveData
import com.example.myreviewmanager.data.Review
import com.example.myreviewmanager.data.ReviewDao

class ReviewRepository(private val reviewDao: ReviewDao) {

    // CORRIGIDO: allReviews é AGORA uma função que DEVE receber o ID do usuário.
    fun getAllReviews(currentUserId: Long): LiveData<List<Review>> {
        return reviewDao.getAllReviews(currentUserId)
    }

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

    // =================================================================
    // NOVO: FUNÇÃO PARA CHECAR DUPLICIDADE
    // =================================================================
    suspend fun getReviewByTmdbIdAndUserId(tmdbId: String, currentUserId: Long): Review? {
        return reviewDao.getReviewByTmdbIdAndUserId(tmdbId, currentUserId)
    }

}