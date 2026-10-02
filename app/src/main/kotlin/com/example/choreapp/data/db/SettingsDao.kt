package com.example.choreapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.choreapp.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert
    suspend fun insert(settings: AppSettings)

    @Update
    suspend fun update(settings: AppSettings)

    @Query("SELECT * FROM app_settings WHERE id = 'settings' LIMIT 1")
    fun getSettings(): Flow<AppSettings?>

    @Query("UPDATE app_settings SET language = :language WHERE id = 'settings'")
    suspend fun updateLanguage(language: String)

    @Query("UPDATE app_settings SET lastDailyReset = :timestamp WHERE id = 'settings'")
    suspend fun updateLastDailyReset(timestamp: Long)
}
