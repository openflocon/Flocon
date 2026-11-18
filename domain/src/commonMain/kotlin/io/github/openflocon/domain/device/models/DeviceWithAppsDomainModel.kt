package io.github.openflocon.domain.device.models

data class DeviceWithAppsDomainModel(
    val device: DeviceDomainModel,
    val apps: List<DeviceAppDomainModel>,
)
