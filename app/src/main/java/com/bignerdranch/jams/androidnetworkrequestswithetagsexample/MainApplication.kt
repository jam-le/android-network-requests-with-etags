package com.bignerdranch.jams.androidnetworkrequestswithetagsexample

import android.app.Application
import android.content.pm.ApplicationInfo
import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}