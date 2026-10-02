package com.example.choreapp.data.repository

import com.example.choreapp.data.db.CommentDao
import com.example.choreapp.domain.model.Comment
import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val commentDao: CommentDao
) {
    suspend fun getRandomComment(category: String): Comment? =
        commentDao.getRandomComment(category)

    suspend fun addComment(comment: Comment) = commentDao.insert(comment)

    suspend fun getCommentCount(category: String): Int =
        commentDao.getCommentCount(category)
}
