package com.example.choreapp

import android.app.Application
import com.example.choreapp.utils.WorkManagerUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChoreApp : Application() {
    override fun onCreate() {
        super.onCreate()
        WorkManagerUtil.scheduleDailyReset(this)
    }
}
