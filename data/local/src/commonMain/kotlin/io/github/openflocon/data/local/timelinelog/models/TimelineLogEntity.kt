package io.github.openflocon.data.local.timelinelog.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName", "timestamp"]),
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
data class TimelineLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deviceId: String,
    val packageName: String,
    val appInstance: Long, // the start time of the mobile app
    val timestamp: Long,
    val type: String, // canonical casing for the reserved types
    val level: String, // TimelineLogLevel.name uppercased: DEBUG|INFO|WARNING|ERROR
    val value: String,
    val paramsJson: String, // Map<String, String> as json
)
