package io.github.openflocon.flocondesktop.features.network.data.datasource.local.model

import androidx.room.Entity
import io.github.openflocon.flocondesktop.features.network.domain.model.NetworkTextFilterColumns

@Entity(
    tableName = "network_filter",
    primaryKeys = [
        "deviceId", "columnName"
    ]
)
data class NetworkFilterEntity(
    val deviceId: String,
    val columnName: NetworkTextFilterColumns,
    val isEnabled: Boolean,
    val itemsAsJson: String,
)
