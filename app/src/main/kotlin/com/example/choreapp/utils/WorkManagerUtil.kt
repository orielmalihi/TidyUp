package com.example.choreapp.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.choreapp.workers.DailyResetWorker
import java.util.concurrent.TimeUnit

object WorkManagerUtil {
    private const val DAILY_RESET_WORK_NAME = "daily_reset_chores"

    fun scheduleDailyReset(context: Context) {
        val dailyResetRequest = PeriodicWorkRequestBuilder<DailyResetWorker>(
            1,
            TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DAILY_RESET_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyResetRequest
        )
    }
}
