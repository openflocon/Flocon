package io.github.openflocon.data.local.crashreporter.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName"]),
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
data class CrashReportEntity(
    @PrimaryKey
    val crashId: String,

    val deviceId: String,
    val packageName: String,
    
    val timestamp: Long,
    val exceptionType: String,
    val exceptionMessage: String,
    val stackTrace: String,
)
