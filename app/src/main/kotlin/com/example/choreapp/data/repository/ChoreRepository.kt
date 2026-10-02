package com.example.choreapp.data.repository

import com.example.choreapp.data.db.ChoreDao
import com.example.choreapp.domain.model.Chore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChoreRepository @Inject constructor(
    private val choreDao: ChoreDao
) {
    fun getAllChores(): Flow<List<Chore>> = choreDao.getAllChores()

    suspend fun addChore(chore: Chore) = choreDao.insert(chore)

    suspend fun updateChore(chore: Chore) = choreDao.update(chore)

    suspend fun deleteChore(chore: Chore) = choreDao.delete(chore)

    suspend fun deleteAllChores() = choreDao.deleteAll()

    suspend fun getChoreById(id: String): Chore? = choreDao.getChoreById(id)
}
