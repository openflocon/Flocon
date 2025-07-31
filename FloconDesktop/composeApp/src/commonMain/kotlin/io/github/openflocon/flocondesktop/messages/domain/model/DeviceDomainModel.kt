package io.github.openflocon.flocondesktop.messages.domain.model

import io.github.openflocon.flocondesktop.DeviceId

data class DeviceDomainModel(
    val appName: String,
    val deviceName: String,
    val appPackageName: String,
    val deviceId: DeviceId,
)
