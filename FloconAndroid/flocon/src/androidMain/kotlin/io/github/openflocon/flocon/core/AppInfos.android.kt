package io.github.openflocon.flocon.core

import android.content.Context
import android.os.Build
import android.provider.Settings
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.utils.AppUtils

private fun deviceId(appContext: Context): String {
    return Settings.Secure.getString(
        appContext.contentResolver,
        Settings.Secure.ANDROID_ID,
    )
}

private fun deviceName(): String {
    return "${Build.MANUFACTURER} ${Build.MODEL}"
}

internal actual fun getAppInfos(floconContext: FloconContext): AppInfos {
    val appContext = floconContext.appContext
    return AppInfos(
        deviceId = deviceId(floconContext.appContext),
        deviceName = deviceName(),
        appName = AppUtils.getAppName(appContext),
        appPackageName = AppUtils.getAppPackageName(appContext)
    )
}
