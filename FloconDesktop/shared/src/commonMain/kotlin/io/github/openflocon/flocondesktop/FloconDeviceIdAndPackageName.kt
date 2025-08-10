package io.github.openflocon.flocondesktop

import kotlinx.serialization.Serializable

@Serializable
data class FloconDeviceIdAndPackageName(
    val deviceId: DeviceId,
    val packageName: String,
)
