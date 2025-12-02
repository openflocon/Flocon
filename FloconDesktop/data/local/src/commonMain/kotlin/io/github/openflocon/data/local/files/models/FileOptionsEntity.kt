package io.github.openflocon.data.local.files.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName"]),
    ],
    primaryKeys = [
        "deviceId",
        "packageName"
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
data class FileOptionsEntity(
    val deviceId: String,
    val packageName: String,
    val withFoldersSize: Boolean,
)
