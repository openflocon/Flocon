package io.github.openflocon.domain.models

typealias DeviceSharedPreferenceId = String

data class DeviceSharedPreferenceDomainModel(
    val id: DeviceSharedPreferenceId,
    val name: String,
)
