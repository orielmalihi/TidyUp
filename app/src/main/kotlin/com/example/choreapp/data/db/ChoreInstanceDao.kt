package com.example.choreapp.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.choreapp.domain.model.ChoreInstance
import com.example.choreapp.domain.model.ChoreStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ChoreInstanceDao {
    @Insert
    suspend fun insert(instance: ChoreInstance)

    @Update
    suspend fun update(instance: ChoreInstance)

    @Delete
    suspend fun delete(instance: ChoreInstance)

    @Query("SELECT * FROM chore_instances WHERE date = :date ORDER BY createdAt")
    fun getInstancesByDate(date: String): Flow<List<ChoreInstance>>

    @Query("SELECT * FROM chore_instances WHERE cleanerId = :cleanerId AND date = :date")
    fun getInstancesForCleanerOnDate(cleanerId: String, date: String): Flow<List<ChoreInstance>>

    @Query("SELECT * FROM chore_instances WHERE cleanerId = :cleanerId AND status = :status AND date = :date")
    suspend fun getInstancesByStatus(cleanerId: String, status: ChoreStatus, date: String): List<ChoreInstance>

    @Query("SELECT * FROM chore_instances WHERE id = :id")
    suspend fun getInstanceById(id: String): ChoreInstance?

    @Query("UPDATE chore_instances SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: ChoreStatus)

    @Query("DELETE FROM chore_instances WHERE date < :date")
    suspend fun deleteOldInstances(date: String)
}
