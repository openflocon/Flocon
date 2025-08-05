package io.github.openflocon.flocondesktop.features.images.data.datasources.local.model

import androidx.room.Entity

@Entity(
    primaryKeys = [
        "deviceId",
        "url",
        "time",
    ],
)
data class DeviceImageEntity(
    val deviceId: String,
    val url: String,
    val time: Long,
)
