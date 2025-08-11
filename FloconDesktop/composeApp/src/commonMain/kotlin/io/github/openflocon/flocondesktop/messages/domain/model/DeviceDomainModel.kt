package io.github.openflocon.flocondesktop.messages.domain.model

import com.flocon.data.remote.models.DeviceId

data class DeviceDomainModel(
    val deviceId: DeviceId,
    val deviceName: String,
    val apps: List<DeviceAppDomainModel>,
)
