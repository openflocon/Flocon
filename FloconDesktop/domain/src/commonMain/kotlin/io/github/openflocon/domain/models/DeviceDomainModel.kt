package io.github.openflocon.domain.models

import io.github.openflocon.domain.device.models.DeviceId

data class DeviceDomainModel(
    val deviceId: DeviceId,
    val deviceName: String,
    val apps: List<DeviceAppDomainModel>,
)
