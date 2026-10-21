package com.flocon.sample.android

import android.app.Application

class MainApplication : Application() {
    companion object {
        var instance: MainApplication? = null
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}