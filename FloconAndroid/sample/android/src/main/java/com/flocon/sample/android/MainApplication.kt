package com.flocon.sample.android

import android.app.Application
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig

class MainApplication : Application() {
    companion object {
        var instance: MainApplication? = null
    }
    override fun onCreate() {
        super.onCreate()
        instance = this

        val apiKey = BuildConfig.POSTHOG_API_KEY
        val host = BuildConfig.POSTHOG_HOST
        if (apiKey.isBlank() || host.isBlank()) {
            if (BuildConfig.DEBUG) {
                val missingVariable = if (apiKey.isBlank()) "POSTHOG_API_KEY" else "POSTHOG_HOST"
                error("$missingVariable variable required by PostHog is missing or un-configured, this causes events to be silently missed. This error stops appearing once $missingVariable is configured")
            }
            return
        }

        PostHogAndroid.setup(this, PostHogAndroidConfig(apiKey = apiKey, host = host).apply {
            errorTrackingConfig.autoCapture = true
            logs.serviceName = "flocon-android-sample"
        })
        PostHogAppLogger.info(
            message = "posthog log capture initialized",
            attributes = mapOf("component" to "application"),
        )
    }
}