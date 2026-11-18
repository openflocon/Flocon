package io.github.openflocon.flocondesktop.app.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeviceAppUiModel(
    val name: String,
    val packageName: String,
    val iconEncoded: String?,
)

fun previewDeviceAppUiModel() = DeviceAppUiModel(
    name = "App name",
    packageName = "Package name",
    iconEncoded = null,
)
