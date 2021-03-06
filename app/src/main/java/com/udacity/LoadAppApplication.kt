package com.udacity

import android.app.Application
import timber.log.Timber

class LoadAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree());
        }
    }
}