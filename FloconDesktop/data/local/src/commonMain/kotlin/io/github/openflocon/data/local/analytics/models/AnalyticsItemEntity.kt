package io.github.openflocon.data.local.analytics.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity

@Entity(
    indices = [
        Index(value = ["deviceId", "packageName", "analyticsTableId"]),
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
data class AnalyticsItemEntity(
    @PrimaryKey
    val itemId: String,
    val analyticsTableId: String,
    val deviceId: String,
    val packageName: String,
    val appInstance: Long, // the start time of the mobile app
    val createdAt: Long,
    val eventName: String,
    val propertiesColumnsNames: List<String>,
    val propertiesValues: List<String>,
)
