package io.github.openflocon.data.local.dashboard.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity

@Entity(
    indices = [
        Index(value = ["dashboardId"]),
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
data class DashboardEntity(
    @PrimaryKey
    val dashboardId: String,
    val deviceId: String,
    val packageName: String,
)
