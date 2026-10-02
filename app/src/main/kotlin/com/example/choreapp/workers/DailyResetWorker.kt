package com.example.choreapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.choreapp.data.db.ChoreAppDatabase
import com.example.choreapp.utils.DateUtils

class DailyResetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = ChoreAppDatabase.getDatabase(applicationContext)
            val dailyScoreDao = database.dailyScoreDao()
            val settingsDao = database.settingsDao()

            val today = DateUtils.getTodayDate()
            
            dailyScoreDao.deleteScoresForDate(today)
            settingsDao.updateLastDailyReset(System.currentTimeMillis())

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
