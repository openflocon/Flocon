package io.github.openflocon.flocondesktop.messages.domain.model

import io.github.openflocon.flocondesktop.DeviceId

data class DeviceIdAndPackageName(
    val deviceId: DeviceId,
    val packageName: String
)
