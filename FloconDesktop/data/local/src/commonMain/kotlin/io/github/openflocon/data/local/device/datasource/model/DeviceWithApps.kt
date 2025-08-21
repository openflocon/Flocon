package io.github.openflocon.data.local.device.datasource.model

import androidx.room.Embedded
import androidx.room.Relation

data class DeviceWithApps(
    @Embedded
    val device: DeviceEntity,
    @Relation(
        parentColumn = "deviceId",
        entityColumn = "parentDeviceId"
    )
    val apps: List<DeviceAppEntity>
)
