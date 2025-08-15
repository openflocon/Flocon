package io.github.openflocon.domain.adb.model

import io.github.openflocon.domain.device.models.DeviceId

data class DeviceWithSerialDomainModel(
    val deviceId: DeviceId,
    val serial: String,
)
