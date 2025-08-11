package io.github.openflocon.domain.sharedpreference.models

typealias DeviceSharedPreferenceId = String

data class DeviceSharedPreferenceDomainModel(
    val id: DeviceSharedPreferenceId,
    val name: String,
)
