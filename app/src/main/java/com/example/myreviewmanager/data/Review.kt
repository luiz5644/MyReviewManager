package com.example.myreviewmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,

    // NOVO: Campo para armazenar o ID do usuário que criou a review
    val userId: Long,

    val tmdbId: String? = null,

    val description: String,
    val priority: Long = 1,
    val createdAt: Long = Date().time
){
    fun getPriorityColor(): Long {
        return when(priority){
            3L -> 0xFFFF5252
            2L -> 0xFFFFB74D
            else -> 0xFF4CAF50
        }
    }

    fun getPriorityText(): String {
        return when (priority) {
            3L -> "Ruim"
            2L -> "Médio"
            else -> "Bom"
        }
    }
}