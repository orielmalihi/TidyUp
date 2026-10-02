package com.example.choreapp.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.choreapp.domain.model.Cleaner
import kotlinx.coroutines.flow.Flow

@Dao
interface CleanerDao {
    @Insert
    suspend fun insert(cleaner: Cleaner)

    @Update
    suspend fun update(cleaner: Cleaner)

    @Delete
    suspend fun delete(cleaner: Cleaner)

    @Query("SELECT * FROM cleaners ORDER BY name")
    fun getAllCleaners(): Flow<List<Cleaner>>

    @Query("SELECT * FROM cleaners WHERE id = :id")
    suspend fun getCleanerById(id: String): Cleaner?

    @Query("DELETE FROM cleaners WHERE id = :id")
    suspend fun deleteById(id: String)
}
