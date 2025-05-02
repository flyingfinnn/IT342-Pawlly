package com.sysinteg.pawlly

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PawllyApplication : Application() {
    companion object {
        private lateinit var instance: PawllyApplication

        fun getAppContext(): Application {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}