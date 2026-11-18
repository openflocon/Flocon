package io.github.openflocon.domain.device.models

// the device selected in the app, with the app selected on it (null while none is known)
data class CurrentDeviceAndAppDomainModel(
    val device: DeviceDomainModel,
    val app: DeviceAppDomainModel?,
)
