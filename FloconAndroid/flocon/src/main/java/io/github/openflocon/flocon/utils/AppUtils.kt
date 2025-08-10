package io.github.openflocon.flocon.utils

import android.content.Context

internal object AppUtils {
    fun getAppName(context: Context): String {
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) {
            applicationInfo.nonLocalizedLabel?.toString() ?: "Unknown"
        } else {
            context.getString(stringId)
        }
    }

    fun getAppPackageName(context: Context): String {
        return context.packageName
    }
}