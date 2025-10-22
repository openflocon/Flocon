package io.github.openflocon.data.local.device.datasource.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeviceEntity(
    @PrimaryKey
    val deviceId: String,
    val deviceName: String,
    val platform: String,
)

