package com.example.choreapp.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.choreapp.domain.model.AllTimeScore
import kotlinx.coroutines.flow.Flow

@Dao
interface AllTimeScoreDao {
    @Insert
    suspend fun insert(score: AllTimeScore)

    @Update
    suspend fun update(score: AllTimeScore)

    @Delete
    suspend fun delete(score: AllTimeScore)

    @Query("SELECT * FROM all_time_scores ORDER BY totalPoints DESC")
    fun getAllTimeScores(): Flow<List<AllTimeScore>>

    @Query("SELECT * FROM all_time_scores WHERE cleanerId = :cleanerId")
    suspend fun getScoreForCleaner(cleanerId: String): AllTimeScore?

    @Query("UPDATE all_time_scores SET totalPoints = totalPoints + :addPoints WHERE cleanerId = :cleanerId")
    suspend fun addPointsToCleaner(cleanerId: String, addPoints: Int)

    @Query("SELECT totalPoints FROM all_time_scores WHERE cleanerId = :cleanerId")
    suspend fun getTotalPoints(cleanerId: String): Int?
}
