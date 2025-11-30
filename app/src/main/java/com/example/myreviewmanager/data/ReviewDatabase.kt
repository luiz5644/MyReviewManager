package com.example.myreviewmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Review::class], version = 1, exportSchema = false)
abstract class ReviewDatabase: RoomDatabase(){
    abstract fun reviewDao(): ReviewDao

    companion object{
        @Volatile
        private var INSTANCE: ReviewDatabase? = null

        fun getDatabase(context: Context): ReviewDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReviewDatabase::class.java,
                    "review_database"
                ).build()
                INSTANCE = instance
                instance

            }
        }

    }

}