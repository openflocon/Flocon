package com.florent37.flocondesktop.features.sharedpreferences.ui.model

import com.florent37.flocondesktop.features.database.domain.model.DeviceDataBaseId

data class DeviceSharedPrefUiModel(
    val id: DeviceDataBaseId,
    val name: String,
)

fun previewDeviceSharedPrefUiModel(id: String = "id") = DeviceSharedPrefUiModel(
    id = id,
    name = "sharedPref_$id",
)
