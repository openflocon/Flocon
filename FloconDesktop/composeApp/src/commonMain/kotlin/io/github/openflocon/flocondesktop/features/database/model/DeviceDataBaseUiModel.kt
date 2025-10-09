package io.github.openflocon.flocondesktop.features.database.model

import io.github.openflocon.domain.database.models.DeviceDataBaseId

data class DeviceDataBaseUiModel(
    val id: DeviceDataBaseId,
    val name: String,
    val isSelected: Boolean,
    val tables: List<TableUiModel>?,
)

data class TableUiModel(
    val name: String,
    val columns: List<ColumnUiModel>
) {
    data class ColumnUiModel(
        val name: String,
        val type: String,
        val isPrimaryKey: Boolean,
    )
}

fun previewDeviceDataBaseUiModel(id: String = "id") = DeviceDataBaseUiModel(
    id = id,
    name = "database.db",
    isSelected = false,
    tables = null,
)
