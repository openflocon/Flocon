package io.github.openflocon.flocondesktop.main.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeviceAppUiModel(
    val name: String,
    val packageName: String
)

fun previewDeviceAppUiModel() = DeviceAppUiModel(
    name = "App name",
    packageName = "Package name"
)
