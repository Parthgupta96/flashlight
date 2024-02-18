package com.dailytech.flashlight

import android.app.Application
import timber.log.Timber

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RemoteConfig.initialize()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}