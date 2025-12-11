package com.example.myreviewmanager.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ReviewDao {

    // CORRIGIDO: Agora a busca requer o ID do usuário
    @Query("SELECT * FROM reviews WHERE userId = :currentUserId ORDER BY createdAt DESC")
    fun getAllReviews(currentUserId: Long): LiveData<List<Review>>

    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    suspend fun getReviewById(reviewId: Long): Review

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review): Long

    @Update
    suspend fun updateReview(review: Review)

    @Delete
    suspend fun deleteReview(review: Review)

    @Query("DELETE FROM reviews WHERE id = :reviewId")
    suspend fun deleteReviewById(reviewId: Long)
}