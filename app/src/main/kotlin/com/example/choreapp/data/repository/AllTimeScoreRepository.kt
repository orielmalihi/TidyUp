package com.example.choreapp.data.repository

import com.example.choreapp.data.db.AllTimeScoreDao
import com.example.choreapp.domain.model.AllTimeScore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AllTimeScoreRepository @Inject constructor(
    private val allTimeScoreDao: AllTimeScoreDao
) {
    fun getAllTimeScores(): Flow<List<AllTimeScore>> = allTimeScoreDao.getAllTimeScores()

    suspend fun addScore(score: AllTimeScore) = allTimeScoreDao.insert(score)

    suspend fun updateScore(score: AllTimeScore) = allTimeScoreDao.update(score)

    suspend fun getScoreForCleaner(cleanerId: String): AllTimeScore? =
        allTimeScoreDao.getScoreForCleaner(cleanerId)

    suspend fun addPointsToCleaner(cleanerId: String, points: Int) =
        allTimeScoreDao.addPointsToCleaner(cleanerId, points)

    suspend fun getTotalPoints(cleanerId: String): Int? =
        allTimeScoreDao.getTotalPoints(cleanerId)
}
