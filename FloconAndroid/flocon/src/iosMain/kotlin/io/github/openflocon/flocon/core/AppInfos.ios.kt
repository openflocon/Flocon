package io.github.openflocon.flocon.core

import io.github.openflocon.flocon.FloconContext
import platform.Foundation.NSBundle
import platform.Foundation.NSUUID
import platform.UIKit.UIDevice

private fun deviceId(): String {
    // Identifiant unique pour l’appareil (généré et persistant tant que l’app est installée)
    val uuid = NSUUID.UUID().UUIDString
    return uuid
}

private fun deviceName(): String {
    val device = UIDevice.currentDevice
    return "${device.systemName()} ${device.model}"
}

internal actual fun getAppInfos(floconContext: FloconContext): AppInfos {
    val bundle = NSBundle.mainBundle
    val appName = bundle.objectForInfoDictionaryKey("CFBundleName") as? String ?: "Unknown"
    val appPackageName = bundle.bundleIdentifier ?: "Unknown"

    return AppInfos(
        deviceId = deviceId(),
        deviceName = deviceName(),
        appName = appName,
        appPackageName = appPackageName,
        platform = "ios",
    )
}