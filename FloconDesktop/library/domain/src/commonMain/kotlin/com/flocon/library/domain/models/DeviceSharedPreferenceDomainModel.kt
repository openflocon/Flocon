package com.flocon.library.domain.models

typealias DeviceSharedPreferenceId = String

data class DeviceSharedPreferenceDomainModel(
    val id: DeviceSharedPreferenceId,
    val name: String,
)
