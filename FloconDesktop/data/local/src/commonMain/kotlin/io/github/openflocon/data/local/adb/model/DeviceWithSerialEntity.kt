package io.github.openflocon.data.local.adb.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import io.github.openflocon.data.local.device.datasource.model.DeviceEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DeviceEntity::class,
            parentColumns = ["deviceId"],
            childColumns = ["deviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class DeviceWithSerialEntity(
    @PrimaryKey
    val deviceId: String,
    val serial: String
)
