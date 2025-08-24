package io.github.openflocon.data.local.images.models

import androidx.room.Entity

@Entity(
    primaryKeys = [
        "deviceId",
        "packageName",
        "url",
        "time",
    ],
)
data class DeviceImageEntity(
    val deviceId: String,
    val packageName: String,
    val url: String,
    val time: Long,
)
