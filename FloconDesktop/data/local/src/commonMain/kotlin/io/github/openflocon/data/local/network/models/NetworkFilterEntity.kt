package io.github.openflocon.data.local.network.models

import androidx.room.Entity
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns

@Entity(
    tableName = "network_filter",
    primaryKeys = [
        "deviceId", "columnName",
    ],
)
data class NetworkFilterEntity(
    val deviceId: String,
    val columnName: NetworkTextFilterColumns,
    val isEnabled: Boolean,
    val itemsAsJson: String,
)
