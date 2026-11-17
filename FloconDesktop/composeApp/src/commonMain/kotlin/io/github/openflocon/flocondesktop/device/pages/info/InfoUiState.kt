package io.github.openflocon.flocondesktop.device.pages.info

import androidx.compose.runtime.Immutable

@Immutable
data class InfoUiState(
    val model: String,
    val brand: String,
    val versionRelease: String,
    val versionSdk: String,
    val serialNumber: String,
    val battery: String
)

fun previewInfoUiState() = InfoUiState(
    model = "",
    brand = "",
    versionRelease = "",
    versionSdk = "",
    serialNumber = "",
    battery = ""
)
