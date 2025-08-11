package io.github.openflocon.flocondesktop.features.database.ui.model

import io.github.openflocon.domain.models.DeviceDataBaseId

data class DeviceDataBaseUiModel(
    val id: DeviceDataBaseId,
    val name: String,
)

fun previewDeviceDataBaseUiModel(id: String = "id") = DeviceDataBaseUiModel(
    id = id,
    name = "database.db",
)
