package com.example.choreapp.data.repository

import com.example.choreapp.data.db.CleanerDao
import com.example.choreapp.domain.model.Cleaner
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CleanerRepository @Inject constructor(
    private val cleanerDao: CleanerDao
) {
    fun getAllCleaners(): Flow<List<Cleaner>> = cleanerDao.getAllCleaners()

    suspend fun addCleaner(cleaner: Cleaner) = cleanerDao.insert(cleaner)

    suspend fun updateCleaner(cleaner: Cleaner) = cleanerDao.update(cleaner)

    suspend fun deleteCleaner(cleaner: Cleaner) = cleanerDao.delete(cleaner)

    suspend fun getCleanerById(id: String): Cleaner? = cleanerDao.getCleanerById(id)
}
