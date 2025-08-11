package io.github.openflocon.flocondesktop.features.network.data.datasource.local.model

import androidx.room.Entity
import com.flocon.library.domain.models.NetworkTextFilterColumns

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
