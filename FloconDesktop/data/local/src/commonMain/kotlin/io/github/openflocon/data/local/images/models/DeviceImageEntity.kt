package io.github.openflocon.data.local.images.models

import androidx.room.Entity
import androidx.room.ForeignKey
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity

@Entity(
    primaryKeys = [
        "deviceId",
        "packageName",
        "url",
        "time",
    ],
    foreignKeys = [
        ForeignKey(
            entity = DeviceAppEntity::class,
            parentColumns = ["deviceId", "packageName"],
            childColumns = ["deviceId", "packageName"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class DeviceImageEntity(
    val deviceId: String,
    val packageName: String,
    val url: String,
    val time: Long,
    val headersJsonEncoded: String,
)
