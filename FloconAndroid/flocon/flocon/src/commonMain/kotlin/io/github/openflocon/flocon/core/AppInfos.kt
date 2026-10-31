package io.github.openflocon.flocon.core

import io.github.openflocon.flocon.FloconContext

internal data class AppInfos(
    val deviceId: String,
    val deviceName: String,
    val appName: String,
    val appPackageName: String,
    val platform: String,
)

internal expect fun getAppInfos(floconContext: FloconContext) : AppInfos