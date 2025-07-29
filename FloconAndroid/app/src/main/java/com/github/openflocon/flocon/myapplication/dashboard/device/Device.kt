package com.github.openflocon.flocon.myapplication.dashboard.device

import android.app.Activity
import android.os.Build
import android.util.DisplayMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


data class Device(
    val name: String,
    val androidVersion: String,
    val language: String,
    val width: Int,
    val height: Int,
    val density: Float,
    val darkTheme: Boolean,
)

val deviceFlow = MutableStateFlow<Device?>(null)

fun initializeDeviceFlow(activity: Activity) {
    val language = activity.resources.configuration.locales.get(0).language

    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

    deviceFlow.update {
        Device(
            name = "${Build.BRAND} ${Build.MODEL}",
            androidVersion = Build.VERSION.RELEASE,
            language = language,
            width = displayMetrics.widthPixels,
            height = displayMetrics.heightPixels,
            density = displayMetrics.density,
            darkTheme = true,
        )
    }
}