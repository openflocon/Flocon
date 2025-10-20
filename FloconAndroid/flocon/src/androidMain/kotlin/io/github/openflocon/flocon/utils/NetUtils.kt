package io.github.openflocon.flocon.utils

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build

// copied from flipper
internal object NetUtils {
    fun isRunningOnStockEmulator(): Boolean {
        return Build.FINGERPRINT.contains("generic") && !Build.FINGERPRINT.contains("vbox")
    }

    fun isRunningOnGenymotion(): Boolean {
        return Build.FINGERPRINT.contains("vbox")
    }
    fun getServerHost(context: Context): String {
        if (isRunningOnStockEmulator() && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // adb reverse was added in lollipop, so before this
            // hard code host ip address.
            // This means it will only work on emulators, not physical devices.
            return "10.0.2.2"
        } else if (isRunningOnGenymotion()) {
            // This is hand-wavy but works on but ipv4 and ipv6 genymotion
            val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val info = wifi.getConnectionInfo()
            val ip = info.getIpAddress()
            return String.format(
                "%d.%d.%d.2",
                (ip and 0xff),
                (ip shr 8 and 0xff),
                (ip shr 16 and 0xff)
            )
        } else {
            // Running on physical device or modern stock emulator.
            // Flipper desktop will run `adb reverse` to forward the ports.
            return "localhost"
        }
    }
}