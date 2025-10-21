package io.github.openflocon.flocon.core

import io.github.openflocon.flocon.FloconContext
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.net.UnknownHostException


internal actual fun getAppInfos(floconContext: FloconContext): AppInfos {
    return AppInfos(
        deviceId = getMacAddress(),
        deviceName = getHostName(),
        appName = floconContext.appName,
        appPackageName = floconContext.packageName,
        platform = "desktop",
    )
}

private fun getHostName(): String {
    try {
        return InetAddress.getLocalHost().hostName
    } catch (e: UnknownHostException) {
        return "Inconnu"
    }
}

private fun getMacAddress(): String {
    try {
        val ip = InetAddress.getLocalHost()
        val network = NetworkInterface.getByInetAddress(ip)
        if (network == null) return "Inconnu"

        val mac = network.hardwareAddress
        if (mac == null) return "Inconnu"

        val sb = StringBuilder()
        for (i in mac.indices) {
            sb.append(String.format("%02X%s", mac[i], if (i < mac.size - 1) "-" else ""))
        }
        return sb.toString()
    } catch (e: UnknownHostException) {
        return "Inconnu"
    } catch (e: SocketException) {
        return "Inconnu"
    }
}