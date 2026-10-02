package com.example.choreapp.data.repository

import com.example.choreapp.data.db.ChoreInstanceDao
import com.example.choreapp.domain.model.ChoreInstance
import com.example.choreapp.domain.model.ChoreStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChoreInstanceRepository @Inject constructor(
    private val choreInstanceDao: ChoreInstanceDao
) {
    fun getInstancesByDate(date: String): Flow<List<ChoreInstance>> =
        choreInstanceDao.getInstancesByDate(date)

    fun getInstancesForCleanerOnDate(cleanerId: String, date: String): Flow<List<ChoreInstance>> =
        choreInstanceDao.getInstancesForCleanerOnDate(cleanerId, date)

    suspend fun addInstance(instance: ChoreInstance) = choreInstanceDao.insert(instance)

    suspend fun updateInstance(instance: ChoreInstance) = choreInstanceDao.update(instance)

    suspend fun deleteInstance(instance: ChoreInstance) = choreInstanceDao.delete(instance)

    suspend fun getInstanceById(id: String): ChoreInstance? = choreInstanceDao.getInstanceById(id)

    suspend fun updateInstanceStatus(id: String, status: ChoreStatus) =
        choreInstanceDao.updateStatus(id, status)

    suspend fun getInstancesByStatus(cleanerId: String, status: ChoreStatus, date: String): List<ChoreInstance> =
        choreInstanceDao.getInstancesByStatus(cleanerId, status, date)
}
