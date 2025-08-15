package io.github.openflocon.data.local.adb.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeviceWithSerialEntity(
    @PrimaryKey
    val deviceId: String,
    val serial: String
)
