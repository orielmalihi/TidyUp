package com.example.choreapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.choreapp.data.db.ChoreAppDatabase
import com.example.choreapp.utils.DateUtils

/**
 * Today's scores are keyed by date, so every new day starts at zero by itself.
 * This worker only tidies the database: it drops old daily scores and chores,
 * while the all-time totals are left untouched.
 */
class DailyResetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = ChoreAppDatabase.getDatabase(applicationContext)
            val today = DateUtils.getTodayDate()

            database.dailyScoreDao().deleteOldScores(today)
            database.choreInstanceDao().deleteOldInstances(today)
            database.settingsDao().updateLastDailyReset(System.currentTimeMillis())

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}