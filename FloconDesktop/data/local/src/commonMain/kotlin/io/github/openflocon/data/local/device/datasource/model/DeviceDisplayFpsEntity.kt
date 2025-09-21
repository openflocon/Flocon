package io.github.openflocon.data.local.device.datasource.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DeviceEntity::class,
            parentColumns = ["deviceId"],
            childColumns = ["deviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = [
        "deviceId",
        "packageName"
    ]
)
data class DeviceDisplayFpsEntity(
    val deviceId: String,
    val packageName: String,
    val displaysFps: Boolean,
)
