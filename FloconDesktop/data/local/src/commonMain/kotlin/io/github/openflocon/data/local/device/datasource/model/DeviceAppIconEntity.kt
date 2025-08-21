package io.github.openflocon.data.local.device.datasource.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DeviceAppEntity::class,
            parentColumns = ["packageName"],
            childColumns = ["appPackageName"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    primaryKeys = [
        "deviceId",
        "appPackageName"
    ]
)
data class DeviceAppIconEntity(
    val deviceId: String,
    val appPackageName: String,
    val iconEncoded: String, // base64
)
