package io.github.openflocon.flocondesktop.features.proxy

import co.touchlab.kermit.Logger
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import java.net.ServerSocket

/**
 * Links the traffic of the proxy to the Android phones adb sees, so that it is shown under the real phone
 * (as a "Proxy" app next to the app of the SDK) instead of a "Proxy <ip>" device:
 * - a phone plugged with `adb reverse`: all of them reach the proxy from 127.0.0.1, so each one gets its own
 *   proxy port, `adb -s <serial> reverse tcp:<proxy port> tcp:<its port>`: the phone keeps 127.0.0.1:<proxy port>.
 * - a phone on the Wi-Fi: by its Wi-Fi address (`ip addr` on the phone, or the serial of adb over Wi-Fi).
 *
 * The device of a phone is the one of its SDK app when Flocon knows its serial, else a device made from adb.
 */
class ProxyDeviceLinker(
    private val adbRepository: AdbRepository,
    private val settingsRepository: SettingsRepository,
    private val devicesRepository: DevicesRepository,
) {

    /**
     * Keeps the proxy in line with the phones until cancelled: every [REFRESH_MS], or at once when
     * [refreshRequests] gets an item (a phone the proxy does not know used it). Removes its `adb reverse` at the end.
     * [send] writes one command line to the proxy.
     */
    suspend fun run(
        proxyPort: Int,
        caPort: Int,
        refreshRequests: ReceiveChannel<Unit>,
        send: (String) -> Unit,
    ) {
        // the phones whose `adb reverse` is set, with their own proxy port
        val portBySerial = mutableMapOf<String, Int>()
        val adbDeviceBySerial = mutableMapOf<String, ProxyLinkedDevice>()
        var lastCommand: String? = null
        try {
            while (true) {
                val adbPath = settingsRepository.getAdbPath()
                if (adbPath != null) {
                    val phones = findPhones(adbPath, proxyPort, caPort, portBySerial, adbDeviceBySerial)
                    val command = buildProxyDevicesCommand(phones, portBySerial).toString()
                    if (command != lastCommand) {
                        send(command)
                        lastCommand = command
                    }
                }
                withTimeoutOrNull(REFRESH_MS) { refreshRequests.receive() }
            }
        } finally {
            withContext(NonCancellable) {
                val adbPath = settingsRepository.getAdbPath() ?: return@withContext
                portBySerial.keys.forEach { serial ->
                    adb(adbPath, serial, "reverse --remove tcp:$proxyPort")
                    adb(adbPath, serial, "reverse --remove tcp:$caPort")
                }
            }
        }
    }

    private suspend fun findPhones(
        adbPath: String,
        proxyPort: Int,
        caPort: Int,
        portBySerial: MutableMap<String, Int>,
        adbDeviceBySerial: MutableMap<String, ProxyLinkedDevice>,
    ): List<ProxyPhone> {
        val serials = adb(adbPath, null, "devices")?.let(::parseAdbSerials).orEmpty()
        // unplugged: its `adb reverse` is gone with it, it is set again when it comes back
        portBySerial.keys.retainAll(serials.toSet())

        val sdkDeviceIdsBySerial = adbRepository.devicesWithSerial.first().groupBy({ it.serial }, { it.deviceId })
        val knownDevices = devicesRepository.devices.first().associateBy { it.deviceId }

        return serials.map { serial ->
            if (serial !in portBySerial) {
                val port = ServerSocket(0).use { it.localPort }
                val reversed = adb(adbPath, serial, "reverse tcp:$proxyPort tcp:$port") != null &&
                    adb(adbPath, serial, "reverse tcp:$caPort tcp:$caPort") != null
                if (reversed) portBySerial[serial] = port
            }
            val sdkDevice = sdkDeviceIdsBySerial[serial].orEmpty().firstNotNullOfOrNull { knownDevices[it] }
            val device = if (sdkDevice != null) {
                // the connection type of the SDK is kept: adb works on it with the serial Flocon knows
                ProxyLinkedDevice(sdkDevice.deviceId, sdkDevice.deviceName, sdkDevice.platform, connectionType = null)
            } else {
                adbDeviceBySerial.getOrPut(serial) { adbDevice(adbPath, serial) }
            }
            val wifiIps = adb(adbPath, serial, "shell ip -4 -o addr show")?.let(::parseIpv4s).orEmpty()
            ProxyPhone(serial = serial, ips = (wifiIps + listOfNotNull(ipOfAdbSerial(serial))).distinct(), device = device)
        }
    }

    /** A phone without the SDK: named like the SDK names it, adb stays off (Flocon has no serial for this device). */
    private fun adbDevice(adbPath: String, serial: String): ProxyLinkedDevice {
        fun prop(name: String) = adb(adbPath, serial, "shell getprop $name")?.trim().orEmpty()
        val id = prop("ro.serialno").ifBlank { serial }
        val name = "${prop("ro.product.manufacturer")} ${prop("ro.product.model")}".trim().ifBlank { serial }
        return ProxyLinkedDevice(deviceId = "android-$id", deviceName = name, platform = "android", connectionType = "wifi")
    }

    private fun adb(adbPath: String, serial: String?, command: String): String? = adbRepository.executeAdbCommand(
        adbPath = adbPath,
        deviceSerial = serial,
        command = command,
    ).fold(
        doOnFailure = {
            Logger.d { "flocon-proxy: adb $command failed on $serial: ${it.message}" }
            null
        },
        doOnSuccess = { it },
    )

    private companion object {
        const val REFRESH_MS = 5_000L
    }
}

/** The device the proxy shows the calls of a phone under. [connectionType] null keeps the one of the SDK. */
internal data class ProxyLinkedDevice(
    val deviceId: String,
    val deviceName: String,
    val platform: String,
    val connectionType: String?,
)

internal data class ProxyPhone(
    val serial: String,
    val ips: List<String>,
    val device: ProxyLinkedDevice,
)

/** The serials `adb devices` lists as ready (not offline or unauthorized). */
internal fun parseAdbSerials(output: String): List<String> = output.lines()
    .map { it.trimEnd() }
    .filter { it.endsWith("\tdevice") }
    .map { it.substringBefore('\t') }

private val Ipv4 = Regex("""\binet (\d{1,3}(?:\.\d{1,3}){3})/""")

/** The IPv4 addresses of `ip -4 -o addr show`, without the loopback. */
internal fun parseIpv4s(output: String): List<String> = Ipv4.findAll(output)
    .map { it.groupValues[1] }
    .filterNot { it.startsWith("127.") }
    .distinct()
    .toList()

/** "192.168.1.12:5555" (adb over Wi-Fi) → "192.168.1.12". Null for a USB or mDNS serial. */
internal fun ipOfAdbSerial(serial: String): String? = Regex("""^(\d{1,3}(?:\.\d{1,3}){3}):\d+$""")
    .find(serial)
    ?.groupValues
    ?.get(1)

/** The "devices" command of the proxy (see proxy/src/index.ts). */
internal fun buildProxyDevicesCommand(phones: List<ProxyPhone>, portBySerial: Map<String, Int>): JsonObject = buildJsonObject {
    put("cmd", "devices")
    putJsonObject("ips") {
        phones.forEach { phone -> phone.ips.forEach { ip -> put(ip, phone.device.toJson()) } }
    }
    putJsonObject("ports") {
        phones.forEach { phone -> portBySerial[phone.serial]?.let { put(it.toString(), phone.device.toJson()) } }
    }
}

private fun ProxyLinkedDevice.toJson(): JsonObject = buildJsonObject {
    put("deviceId", deviceId)
    put("deviceName", deviceName)
    put("platform", platform)
    connectionType?.let { put("connectionType", it) }
}
