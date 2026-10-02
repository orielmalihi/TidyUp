package com.example.choreapp.data.repository

import com.example.choreapp.data.db.SettingsDao
import com.example.choreapp.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao
) {
    fun getSettings(): Flow<AppSettings?> = settingsDao.getSettings()

    suspend fun updateLanguage(language: String) = settingsDao.updateLanguage(language)

    suspend fun updateLastDailyReset(timestamp: Long) = settingsDao.updateLastDailyReset(timestamp)

    suspend fun insertSettings(settings: AppSettings) = settingsDao.insert(settings)
}
