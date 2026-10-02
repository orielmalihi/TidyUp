package com.example.choreapp.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.choreapp.domain.model.Chore
import kotlinx.coroutines.flow.Flow

@Dao
interface ChoreDao {
    @Insert
    suspend fun insert(chore: Chore)

    @Update
    suspend fun update(chore: Chore)

    @Delete
    suspend fun delete(chore: Chore)

    @Query("SELECT * FROM chores ORDER BY name")
    fun getAllChores(): Flow<List<Chore>>

    @Query("SELECT * FROM chores WHERE id = :id")
    suspend fun getChoreById(id: String): Chore?

    @Query("DELETE FROM chores WHERE id = :id")
    suspend fun deleteById(id: String)
}
