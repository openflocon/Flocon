package io.github.openflocon.domain.device.models

data class RegisterDeviceWithAppDomainModel(
    val device: DeviceDomainModel,
    val app: DeviceAppDomainModel,
)
