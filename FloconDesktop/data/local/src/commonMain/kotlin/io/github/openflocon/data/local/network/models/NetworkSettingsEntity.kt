package io.github.openflocon.data.local.network.models

import androidx.room.Entity
import androidx.room.ForeignKey
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns

@Entity(
    primaryKeys = [
        "deviceId", "packageName" // maybe only deviceId ?
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
data class NetworkSettingsEntity(
    val deviceId: String,
    val packageName: String,
    val valueAsJson: String,
)
