package com.example.choreapp

import android.app.Application
import com.example.choreapp.utils.SoundEffects
import com.example.choreapp.utils.WorkManagerUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChoreApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SoundEffects.init(this)
        WorkManagerUtil.scheduleDailyReset(this)
    }
}
