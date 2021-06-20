package com.apps.skimani.afyafood

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

class AfyaApp:Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
    }
}