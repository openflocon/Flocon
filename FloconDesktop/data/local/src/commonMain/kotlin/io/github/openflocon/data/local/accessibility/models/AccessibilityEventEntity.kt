package io.github.openflocon.data.local.accessibility.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity

@Entity(
    primaryKeys = ["deviceId", "packageName", "id"],
    indices = [
        Index(value = ["deviceId", "packageName", "createdAt"]),
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
data class AccessibilityEventEntity(
    val deviceId: String,
    val packageName: String,
    val id: String,
    val appInstance: Long, // the start time of the mobile app
    val type: String,
    val text: String?,
    val contentDescription: String?,
    val className: String?,
    val screen: String?,
    val bounds: String?,
    val extras: Map<String, String>,
    val createdAt: Long,
)
