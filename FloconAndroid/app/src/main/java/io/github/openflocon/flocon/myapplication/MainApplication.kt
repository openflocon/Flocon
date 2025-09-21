package io.github.openflocon.flocon.myapplication

import android.app.Application
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconLogger

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FloconLogger.enabled = true
        Flocon.initialize(this)
    }
}