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
data class DeviceAppEntity(
    val deviceId: String,
    val name: String,
    val packageName: String,
    val iconEncoded: String?, // base64
    val lastAppInstance: Long, // last start the app has been started
)
