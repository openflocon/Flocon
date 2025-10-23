package io.github.openflocon.flocon.core

import com.russhwolf.settings.NSUserDefaultsSettings
import io.github.openflocon.flocon.FloconContext
import platform.Foundation.NSBundle
import platform.Foundation.NSUUID
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIDevice

private fun getDeviceId() : String {
    val settings = NSUserDefaultsSettings(
        NSUserDefaults.standardUserDefaults()
    )
    val id = settings.getStringOrNull("deviceId")
    return if(id != null) {
        id
    } else {
        val newId = NSUUID.UUID().UUIDString()
        settings.putString("deviceId", newId)
        newId
    }
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
        deviceId = getDeviceId(),
        deviceName = deviceName(),
        appName = appName,
        appPackageName = appPackageName,
        platform = "ios",
    )
}