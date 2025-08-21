package io.github.openflocon.domain.device.models

data class HandleDeviceResultDomainModel(
    val deviceId: String,
    val justConnectedForThisSession: Boolean,
    val isNewDevice: Boolean,
    val isNewApp: Boolean,
)
