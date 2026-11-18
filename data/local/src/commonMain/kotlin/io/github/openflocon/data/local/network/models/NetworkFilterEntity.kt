package io.github.openflocon.data.local.network.models

import androidx.room.Entity
import androidx.room.ForeignKey
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns

@Entity(
    primaryKeys = [
        "deviceId", "columnName",
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
data class NetworkFilterEntity(
    val deviceId: String,
    val packageName: String,
    val columnName: NetworkTextFilterColumns,
    val isEnabled: Boolean,
    val itemsAsJson: String,
)
