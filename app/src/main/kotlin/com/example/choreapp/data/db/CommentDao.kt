package com.example.choreapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.choreapp.domain.model.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert
    suspend fun insert(comment: Comment)

    @Query("SELECT * FROM comments WHERE category = :category ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomComment(category: String): Comment?

    @Query("SELECT * FROM comments WHERE category = :category")
    fun getCommentsByCategory(category: String): Flow<List<Comment>>

    @Query("SELECT COUNT(*) FROM comments WHERE category = :category")
    suspend fun getCommentCount(category: String): Int
}
