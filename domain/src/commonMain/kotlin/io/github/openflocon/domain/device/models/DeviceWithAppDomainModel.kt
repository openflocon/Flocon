package io.github.openflocon.domain.device.models

data class DeviceWithAppDomainModel(
    val device: DeviceDomainModel,
    val app: DeviceAppDomainModel,
)
