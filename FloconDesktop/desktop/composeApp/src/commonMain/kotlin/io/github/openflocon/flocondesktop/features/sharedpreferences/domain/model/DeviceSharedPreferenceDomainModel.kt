package io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model

typealias DeviceSharedPreferenceId = String

data class DeviceSharedPreferenceDomainModel(
    val id: DeviceSharedPreferenceId,
    val name: String,
)
