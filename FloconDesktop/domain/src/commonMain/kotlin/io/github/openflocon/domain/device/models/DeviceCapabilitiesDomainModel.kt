package io.github.openflocon.domain.device.models

data class DeviceCapabilitiesDomainModel(
    val screenshot: Boolean,
    val recordScreen: Boolean,
    val restart: Boolean,
    val files: Boolean,
    val sharedPreferences: Boolean,
    val deeplinks: Boolean,
)
