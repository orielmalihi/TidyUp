package com.example.choreapp.data.repository

import com.example.choreapp.data.db.DailyScoreDao
import com.example.choreapp.domain.model.DailyScore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DailyScoreRepository @Inject constructor(
    private val dailyScoreDao: DailyScoreDao
) {
    fun getScoresForDate(date: String): Flow<List<DailyScore>> =
        dailyScoreDao.getScoresForDate(date)

    suspend fun addScore(score: DailyScore) = dailyScoreDao.insert(score)

    suspend fun updateScore(score: DailyScore) = dailyScoreDao.update(score)

    suspend fun getScoreForCleanerOnDate(cleanerId: String, date: String): DailyScore? =
        dailyScoreDao.getScoreForCleanerOnDate(cleanerId, date)

    suspend fun addPointsToCleanerOnDate(cleanerId: String, date: String, points: Int) =
        dailyScoreDao.addPointsToCleanerOnDate(cleanerId, date, points)

    suspend fun deleteScoresForDate(date: String) = dailyScoreDao.deleteScoresForDate(date)
}
