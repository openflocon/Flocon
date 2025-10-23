package io.github.openflocon.flocon.myapplication.multi

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