package com.example.choreapp

import android.app.Application
import com.example.choreapp.utils.SoundEffects
import com.example.choreapp.utils.WorkManagerUtil
import com.example.choreapp.data.backup.BackupManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class ChoreApp : Application() {
    @Inject
    lateinit var backupManager: BackupManager

    override fun onCreate() {
        super.onCreate()
        backupManager.start(CoroutineScope(SupervisorJob() + Dispatchers.Default))
        SoundEffects.init(this)
        WorkManagerUtil.scheduleDailyReset(this)
    }
}
