package io.github.openflocon.flocondesktop.telemetry

import io.github.openflocon.data.core.device.datasource.remote.RemoteDeviceDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.device.models.DeviceConnectionType
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository

/**
 * Sends [TelemetryEvents.DEVICE_CONNECTED] when an app says "bonjour".
 * The SDK says it again on every reconnection: only the first one of each app launch is sent.
 */
internal class DeviceConnectionTelemetry(
    private val remoteDeviceDataSource: RemoteDeviceDataSource,
    private val telemetryManager: TelemetryManager,
) : MessagesReceiverRepository {

    // messages are handled one after the other (HandleIncomingMessagesUseCase), no concurrent access
    private val trackedLaunches = mutableSetOf<DeviceIdAndPackageNameDomainModel>()

    override val pluginName = listOf(Protocol.FromDevice.Bonjour.Plugin)

    override suspend fun onMessageReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        message: FloconIncomingMessageDomainModel,
    ) {
        if (message.method != Protocol.FromDevice.Bonjour.Method.Bonjour) return
        if (!trackedLaunches.add(deviceIdAndPackageName)) return

        telemetryManager.track(
            event = TelemetryEvents.DEVICE_CONNECTED,
            properties = deviceConnectedProperties(
                platform = message.platform,
                sdk = remoteDeviceDataSource.getSdk(message),
                sdkVersion = message.floconVersionOnDevice,
                connectionType = remoteDeviceDataSource.getConnectionType(message),
            ),
        )
    }

    override suspend fun onDeviceConnected(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isNewDevice: Boolean,
    ) = Unit
}

private val KnownPlatforms = setOf("android", "ios", "desktop")
private val KnownSdks = setOf("kotlin", "swift", "flutter")
private val VersionRegex = Regex("""\d+(\.\d+){0,3}([-+][0-9A-Za-z.-]{1,32})?""")

/** Only known values are sent: these fields come from the SDKs, which could put anything in them. */
internal fun deviceConnectedProperties(
    platform: String,
    sdk: String?,
    sdkVersion: String,
    connectionType: DeviceConnectionType?,
): Map<String, Any> = mapOf(
    "platform" to platform.lowercase().takeIf { it in KnownPlatforms }.orElse("other"),
    "sdk" to when (val name = sdk?.lowercase()) {
        null -> "unknown"
        in KnownSdks -> name
        else -> "other"
    },
    "sdk_version" to sdkVersion.takeIf { VersionRegex.matches(it) }.orElse("unknown"),
    "connection_type" to when (connectionType) {
        DeviceConnectionType.Wifi -> "wifi"
        DeviceConnectionType.Default -> "default"
        null -> "unknown"
    },
)

private fun String?.orElse(fallback: String): String = this ?: fallback
