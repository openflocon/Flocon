package io.github.openflocon.domain.device.models

data class DeviceDomainModel(
    val deviceId: DeviceId,
    val deviceName: String,
    val apps: List<DeviceAppDomainModel>,
)
