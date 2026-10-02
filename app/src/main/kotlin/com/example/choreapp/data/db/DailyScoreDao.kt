package com.example.choreapp.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.choreapp.domain.model.DailyScore
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyScoreDao {
    @Insert
    suspend fun insert(score: DailyScore)

    @Update
    suspend fun update(score: DailyScore)

    @Delete
    suspend fun delete(score: DailyScore)

    @Query("SELECT * FROM daily_scores WHERE date = :date ORDER BY points DESC")
    fun getScoresForDate(date: String): Flow<List<DailyScore>>

    @Query("SELECT * FROM daily_scores WHERE cleanerId = :cleanerId AND date = :date")
    suspend fun getScoreForCleanerOnDate(cleanerId: String, date: String): DailyScore?

    @Query("UPDATE daily_scores SET points = points + :addPoints WHERE cleanerId = :cleanerId AND date = :date")
    suspend fun addPointsToCleanerOnDate(cleanerId: String, date: String, addPoints: Int)

    @Query("DELETE FROM daily_scores WHERE date = :date")
    suspend fun deleteScoresForDate(date: String)

    @Query("DELETE FROM daily_scores WHERE date < :date")
    suspend fun deleteOldScores(date: String)
}
