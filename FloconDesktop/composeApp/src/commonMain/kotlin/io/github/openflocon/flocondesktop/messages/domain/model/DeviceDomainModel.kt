package io.github.openflocon.flocondesktop.messages.domain.model

import io.github.openflocon.flocondesktop.DeviceId

data class DeviceDomainModel(
    val deviceId: DeviceId,
    val deviceName: String,
    val apps: List<DeviceAppDomainModel>,
)
