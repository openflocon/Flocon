package io.github.openflocon.flocondesktop.features.sharedpreferences.ui.model

import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseId

data class DeviceSharedPrefUiModel(
    val id: DeviceDataBaseId,
    val name: String,
)

fun previewDeviceSharedPrefUiModel(id: String = "id") = DeviceSharedPrefUiModel(
    id = id,
    name = "sharedPref_$id",
)
