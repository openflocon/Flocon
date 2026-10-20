package io.github.openflocon.data.local.featureflags.models

import androidx.room.Entity
import androidx.room.ForeignKey
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity

@Entity(
    primaryKeys = ["deviceId", "packageName", "provider", "name"],
    foreignKeys = [
        ForeignKey(
            entity = DeviceAppEntity::class,
            parentColumns = ["deviceId", "packageName"],
            childColumns = ["deviceId", "packageName"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class FeatureFlagEntity(
    val deviceId: String,
    val packageName: String,
    val provider: String,
    val name: String,
    val variant: String,
    val payload: String?,
    val appInstance: Long, // the start time of the mobile app
)
